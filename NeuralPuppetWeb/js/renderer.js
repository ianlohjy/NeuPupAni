function Renderer()
{   // Handles rendering of Animation.data
    
    // GIF Encoder. This is used if web workers are not available 
    this.gif_encoder = new GIFEncoder();
    
    // Web worker GIF Encoder. This is used if web workers are available. 
    // Allows encoding to be done in background and not lock up window
    this.render_worker = null; 

    // Render settings
    this.render_length = 0; // In frames
    this.rendering = false;
    this.rendering_frame = 0;

    // Animation data
    this.animation_data = null; // Copy (not reference) of animation data
    this.rendered_canvas = new Canvasy(); // Where encoded frames will be rendered to
    this.rendered_context = this.rendered_canvas.context;
    this.render_output = null;

    var renderer = this; // A reference to the renderer (use to overcome this scope in callback method)

    Renderer.prototype.setup = function()
    {  
        this.rendered_canvas.width = 256;
        this.rendered_canvas.height = 256;
        /*
        this.rendered_context.fillStyle = 'rgb(100,200,255)';
        this.rendered_context.fillRect(0,0,this.rendered_canvas.width, this.rendered_canvas.height);
        this.rendered_context.drawImage(animation.data.image,0,0,500,500);
        this.rendered_context.fillStyle = 'rgb(25,0,0)';
        this.rendered_context.fillRect(0,0,this.rendered_canvas.width/2, this.rendered_canvas.height/2);
        */
    }

    // Render Worker
    this.render_worker_onmessage = function(event)
    {   // Messages received from render workers
        let command = event.data[0];
        let content = event.data[1];

        if(command == 'addframe')
        {   
            //console.log('Added frame ' + content);
            renderer.when_frame_added();
        }

        if(command == 'show')
        {
            //console.log('Showing final result');
            renderer.show_render(content);
        }
    }

    // Utilities
    Renderer.prototype.update_status = function(message)
    {   //console.log(message);
        document.getElementById('render-status').innerText = message;
    }

    // Render Methods     
    Renderer.prototype.setup_render = function(input_anim, frames)
    {   
        if(input_anim.path.length == 0)
        {    return;
        }

        this.set_animation_data(input_anim);
        if(frames == -1)
        {   this.render_length = this.animation_data.path.length;
        }
        else
        {   this.render_length = frames;
        }
        
        // Set the worker with the same properties if available
        if(window.Worker)
        {
            if(this.render_worker != null)
            {   // If there already is a worker active, kill it
                this.render_worker.terminate();
                console.log('Render worker killed');
            }

            console.log('Web Workers available, will use workers for rendering');
            this.render_worker = new Worker('./js/render_worker.js');
            this.render_worker.onmessage = this.render_worker_onmessage;

            let content =   {repeat: 0,
                            delay: Math.floor(animation.millis_per_frame)};
            this.render_worker.postMessage(['setup', content]);
        }
        else
        {
            this.gif_encoder.setRepeat(0);
            this.gif_encoder.setDelay(Math.floor(animation.millis_per_frame)); 
            this.gif_encoder.setFrameCallback(this.when_frame_added);
        }
    }

    Renderer.prototype.start_render = function()
    {
        if(this.animation_data == null)
        {   this.update_status('Nothing to render. Draw something to render!');
            return;
        }
        else if(this.animation_data.path.length > 0)
        {   this.rendering_frame = 0;
            this.rendering = true;

            if(this.render_worker != null)
            {   this.render_worker.postMessage(['start','']);
            }
            else
            {   this.gif_encoder.start();
            }

            this.update_status('Starting render...');
            this.render_frame(this.rendering_frame);   
        }
        else
        {   this.update_status('Nothing to render. Draw something to render!');
            return;
        }
    }

    Renderer.prototype.render_frame = function(frame)
    {   // This method is continuously called until rendering is complete
        
        if(frame < this.animation_data.path.length)
        {   this.update_status('Rendering ' + frame + ' / ' + (this.animation_data.path.length-1));
            // If the frame to render is less than path length
            let current_pos = this.animation_data.path[frame];
            let grid_pos = this.animation_data.get_grid_position(current_pos.x, current_pos.y);
            
            // Draw image onto render context
            // this.rendered_context.fillStyle = 'rgb('+ Math.random()*255  +',200,255)';
            this.rendered_context.fillStyle = 'rgb('+ Math.floor(255*Math.random()) +',200,255)';
            this.rendered_context.fillRect(0,0,this.rendered_canvas.width, this.rendered_canvas.height);
            
            this.rendered_context.drawImage(this.animation_data.image,
                                            grid_pos.x, grid_pos.y, grid_pos.w, grid_pos.h, 
                                            0, 0, this.rendered_canvas.width, this.rendered_canvas.height);
            
            let image_data = this.rendered_context.getImageData(0,0,this.rendered_canvas.width,this.rendered_canvas.height);
            
            if(this.render_worker != null)
            {   let content = {image_data:image_data, frame:frame}
                this.render_worker.postMessage(['addframe',content]);
            }
            else
            {
                this.gif_encoder.setSize(image_data.width,image_data.height);
                this.gif_encoder.addFrame(image_data.data, true);
            }
        }
        else
        {   this.update_status('Skipped: Frame does not exist!');
            this.finish_render();
        }
    }

    // Render Callback
    this.when_frame_added = function()
    {   // Rendering Callback after frame is added
        //renderer.update_status('FRAME ADDED, MOVING ON...');
        
        if(renderer.rendering_frame < renderer.render_length)
        {   renderer.rendering_frame++;
            renderer.render_frame(renderer.rendering_frame);
        }
        else
        {   renderer.finish_render();
        }
    }

    Renderer.prototype.finish_render = function()
    {
        if(this.render_worker != null)
        {
            this.render_worker.postMessage(['finish','']);
        }
        else
        {
            this.gif_encoder.finish();
            renderer.show_render(this.gif_encoder.stream().getData());
        }

        this.reset_render();
    }

    Renderer.prototype.show_render = function(binary_gif)
    {   this.update_status('Complete! Animation added to grid')
        render_output = 'data:image/gif;base64,'+encode64(binary_gif);

        let image = document.createElement('img');
        image.src = render_output;

        image.classList.add('render_result');
        document.getElementById('render_container').appendChild(image);
    }

    Renderer.prototype.reset_render = function()
    {
        this.rendering = false;
        this.animation_data = null;
        this.rendering_frame = 0;
        this.render_length = 0;
    }

    Renderer.prototype.set_animation_data = function(input_anim)
    {   // Copies animation data
        this.animation_data = new AnimData();
        this.animation_data.image = input_anim.image;
        this.animation_data.image_shape[0] = input_anim.image_shape[0];
        this.animation_data.image_shape[1] = input_anim.image_shape[1];
        this.animation_data.path = Array.from(input_anim.path);
    }

    this.setup();
}