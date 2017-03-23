function Renderer()
{   // Handles rendering of Animation.data
    
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

    Renderer.prototype.setup = function()
    {
    }

    Renderer.prototype.setup_render = function(input_anim, frames)
    {
        this.set_animation_data(input_anim);
        if(frames == -1)
        {   this.render_length = animation_data.path.length;
        }
        else
        {   this.render_length = frames;
        }
        this.gif_encoder.setRepeat(0);
        this.gif_encoder.setDelay(animation.render_millis_per_frame); 
        this.gif_encoder.setFrameCallback(function(){console.log('Frame Added!');});
    }

    Renderer.prototype.start_render = function()
    {
        this.rendering_frame = 0;
        this.rendering = true;
        this.gif_encoder.start();
    }

    Renderer.prototype.while_render = function()
    {
        if(this.rendering)
        {
            this.gif_encoder.addFrame(grid.context);
        }
    }

    Renderer.prototype.finish_render = function()
    {
        this.rendering = false;
        this.gif_encoder.finish();

        let binary_gif = this.gif_encoder.stream().getData() //notice this is different from the as3gif package!
        let data_url = 'data:image/gif;base64,'+encode64(binary_gif);
        
        console.log(data_url);
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