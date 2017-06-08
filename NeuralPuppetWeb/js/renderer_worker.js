function RenderWorker()
{   // Implementation test of renderer with webworkers
    
    // GIF Recorder 
    this.gif_encoder = new GIFEncoder();
    
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

    // Utilities
    Renderer.prototype.update_status = function(message)
    {   //console.log(message);
        document.getElementById('render-status').innerText = message;
    }

    // Render Methods  
    this.when_frame_added = function()
    {   // Rendering Callback
        renderer.update_status('FINISH ADDING FRAME, MOVING ON...');
        if(renderer.rendering_frame < renderer.render_length)
        {   renderer.rendering_frame++;
            renderer.render_frame(renderer.rendering_frame);
        }
        else
        {   renderer.finish_render();
        }
    }

    Renderer.prototype.setup_render = function(input_anim, frames)
    {   
        if(input_anim.path.length == 0)
        {    this.update_status('NOTHING TO RENDER! DRAW SOMETHING');
             return;
        }

        this.set_animation_data(input_anim);
        if(frames == -1)
        {   this.render_length = this.animation_data.path.length;
        }
        else
        {   this.render_length = frames;
        }
        this.gif_encoder.setRepeat(0);
        this.gif_encoder.setDelay(Math.floor(animation.millis_per_frame)); 
        this.gif_encoder.setFrameCallback(this.when_frame_added);
    }

    Renderer.prototype.start_render = function()
    {
        if(this.animation_data == null)
        {   this.update_status('NOTHING TO RENDER! DRAW SOMETHING');
            return;
        }
        else if(this.animation_data.path.length > 0)
        {   this.rendering_frame = 0;
            this.rendering = true;
            this.gif_encoder.start();
            this.update_status('STARTING RENDER...');
            this.render_frame(this.rendering_frame);   
        }
        else
        {   this.update_status('NOTHING TO RENDER! DRAW SOMETHING');
            return;
        }
    }

    Renderer.prototype.render_frame = function(frame)
    {   // This method is continuously called until rendering is complete
        this.update_status('GOING TO RENDER FRAME ' + frame);
        
        if(frame < this.animation_data.path.length)
        {   this.update_status('RENDERING FRAME ' + frame);
            // If the frame to render is less than path length
            let current_pos = this.animation_data.path[frame];
            let grid_pos = this.animation_data.get_grid_position(current_pos.x, current_pos.y);
            // Draw image onto render context
            
            //this.rendered_context.fillStyle = 'rgb('+ Math.random()*255  +',200,255)';
            this.rendered_context.fillStyle = 'rgb('+ Math.floor(255*Math.random()) +',200,255)';
            this.rendered_context.fillRect(0,0,this.rendered_canvas.width, this.rendered_canvas.height);
            
            this.rendered_context.drawImage(this.animation_data.image,
                                            grid_pos.x, grid_pos.y, grid_pos.w, grid_pos.h, 
                                            0, 0, this.rendered_canvas.width, this.rendered_canvas.height);

            this.gif_encoder.addFrame(this.rendered_context);
            console.log("rendered frame " + frame);
        }
        else
        {   this.update_status('SKIPPED! FRAME DOES NOT EXIST');
            this.finish_render();
        }
    }

    Renderer.prototype.finish_render = function()
    {
        this.gif_encoder.finish();

        let binary_gif = this.gif_encoder.stream().getData() //notice this is different from the as3gif package!
        render_ouput = 'data:image/gif;base64,'+encode64(binary_gif);
        
        this.update_status('RENDERING DONE!')
        document.getElementById('render_result').src = render_ouput;

        this.reset_render();
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