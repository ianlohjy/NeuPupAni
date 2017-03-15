function Animation()
{   // Handles everything related to animation editing and playback 
    // Settings
    this.target_fps = 60;
    this.millis_per_frame = 1000/this.target_fps;
    // Animation Data
    this.data = new AnimData();
    // Constants
    const PAUSE  = 0; // This is the default fallback state
    const PLAY   = 1; 
    const RECORD = 2
    // States
    this.mode = PAUSE;
    this.last_playback_time = performance.now();
    // GIF Recording & render settings
    this.gif_encoder = new GIFEncoder();
    this.last_rendered_frame = performance.now();
    this.render_fps = 30;
    this.render_millis_per_frame = 1000/this.render_fps;
    this.render_length = 5000; // In milliseconds
    this.render_start_time = 0;
    this.rendering = false;

    Animation.prototype.update = function(x, y)
    {   if(this.mode == RECORD)
        {   this.record(x,y);
        }
        if(this.rendering)
        {   this.render();
        }
    }

    Animation.prototype.render_start = function()
    {   if(!this.rendering)
        {   this.last_rendered_frame = performance.now();
            render_start_time = performance.now();
            this.rendering = true;

            this.gif_encoder.setRepeat(0);
            this.gif_encoder.setDelay(this.render_millis_per_frame); // Render at 20 frames per second for now 
            this.gif_encoder.start();
            console.log('GIF RENDER STARTED');
        }
        else
        {   console.log('ALREADY RENDERING!');
        }
    }

    Animation.prototype.render = function()
    {
        if(performance.now() - this.render_start_time >= this.render_length)
        {
            this.render_stop();
        }
        else if(performance.now() - this.last_rendered_frame >=  this.render_millis_per_frame)
        {   
            this.last_rendered_frame = performance.now();
            this.gif_encoder.addFrame(grid.context); // << THIS IS BAD, FIX LATER!
            console.log('RENDERING...');
        }
    }

    Animation.prototype.render_stop = function()
    {   this.rendering = false;
        this.gif_encoder.finish();
        console.log('GIF RENDER STOPPED');

        let binary_gif = this.gif_encoder.stream().getData() //notice this is different from the as3gif package!
        let data_url = 'data:image/gif;base64,'+ encode64(binary_gif);
        document.getElementById('render_result').src = data_url;
        //console.log(data_url);
    }

    Animation.prototype.record = function(x, y)
    {   if(performance.now() - this.last_playback_time >= this.millis_per_frame)
        {   this.data.add_point(x,y);
            this.last_playback_time = performance.now();
        }
    }

    Animation.prototype.start_recording = function(x, y)
    {   this.mode = RECORD;
        this.data.clear();
        this.record(x,y);
    }

    Animation.prototype.stop_recording = function()
    {   this.mode = PAUSE;
    }
}

function AnimData()
{   // Handles all animation data
    this.path = new Array();

    // Data Properties
    Object.defineProperty(this, 'size',
    {   get: function(){return this.path.length;}
    });

    AnimData.prototype.add_point = function(x, y)
    {   // Adds a Point object to this.path
        this.path.push(new Point(x,y));
    }

    AnimData.prototype.clear = function()
    {   // Resets this.path
        this.path = new Array();
    }

    // Point Class
    function Point(x, y)
    {   this.x = x;
        this.y = y;
    }
}

