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
    this.last_playback_time = time();
    // Cursor and Frame
    this.current_frame = 0;
    // Cursor should not be accessible to anything else. Use current_cursor_pos() instead
    var cursor_x = 0; 
    var cursor_y = 0;

    Animation.prototype.update = function()
    {   if(this.mode == RECORD)
        {   this.record(cursor_x, cursor_y);
        }
        else if(this.mode == PLAY)
        {
            if(time() - this.last_playback_time >= this.millis_per_frame)
            {
                this.go_to_frame(this.current_frame+1);
                this.last_playback_time = time();
            }
        }
    }

    Animation.prototype.update_cursor = function(input_x, input_y)
    {   cursor_x = input_x;
        cursor_y = input_y;
    }
    
    // Playback
    Animation.prototype.go_to_frame = function(frame)
    {   let new_frame = frame;
        if(new_frame > this.data.path.length-1)
        {   new_frame = 0;
        }
        else if(new_frame < 0)
        {   new_frame = 0;
        }
        this.current_frame = new_frame;
        timeline.update_length()
    }

    Animation.prototype.stop = function()
    {   this.pause();
        this.current_frame = 0;
    }

    Animation.prototype.play = function()
    {   if(!this.is_recording())
        {   this.mode = PLAY;
        }
    }

    Animation.prototype.pause = function()
    {
        this.mode = PAUSE;
    }

    Animation.prototype.toggle_play = function()
    {   // Toggles play/pause
        if(!this.is_recording())
        {
            if(this.is_playing())
            {   this.pause();
            }
            else
            {   this.play();
            }
        }
    }

    Animation.prototype.is_recording = function()
    {   if(this.mode == RECORD)
        {   return true;
        }
        else
        {   return false;
        }
    }

    Animation.prototype.is_playing = function()
    {   if(this.mode == PLAY)
        {   return true;
        }
        else
        {   return false;
        }
    }

    /*
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
    }*/

    // Recording
    Animation.prototype.record = function(x, y)
    {   if(performance.now() - this.last_playback_time >= this.millis_per_frame)
        {   this.data.add_point(x,y);
            this.last_playback_time = performance.now();
            
            // Update position of current frame and update timeline
            this.current_frame = this.data.path.length-1;
            timeline.update_length();
        }
    }

    Animation.prototype.start_recording = function(x, y)
    {   this.mode = RECORD;
        this.data.clear();
        this.record(x,y);
    }

    Animation.prototype.stop_recording = function()
    {   this.pause();
        // Update position of current frame and update timeline
        this.current_frame = this.data.path.length-1;
        timeline.update_length();
    }

    Animation.prototype.current_cursor_pos = function()
    {   
        if(this.mode == RECORD || this.mode == PAUSE)
        {   return new Point(cursor_x, cursor_y);
        }
        else if(this.mode == PLAY)
        {
            if(this.data.path.length !== 0)
            {   return this.data.path[this.current_frame];
            }
            else
            {   return new Point(0, 0);
            }
        }
    }
}

function AnimData()
{   // Handles all animation data
    this.path = new Array();
    // Image Data
    this.image = null;
    this.image_shape = [1, 1];

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

    AnimData.prototype.set_grid_image = function(src, rows, cols)
    {   // Sets up image
        this.image = new Image();
        this.image.src = src;
        this.image_shape = [rows, cols]; 
    }

    AnimData.prototype.get_grid_position = function(amt_x, amt_y)
    {   // Returns the area of the image to show given amt_x & amt_y (0 to 1.0)
        // Find the size of a each grid square
        let div_x = this.image.width / this.image_shape[0];
        let div_y = this.image.height/ this.image_shape[1];
        // Find out which grid square to display
        let loc_x = Math.floor(amt_x * this.image_shape[0]);
        let loc_y = Math.floor(amt_y * this.image_shape[1]);
        // Make sure that grid square is between 0 and grid_shape-1
        if(loc_x < 0){loc_x = 0};
        if(loc_x > this.image_shape[0]-1){loc_x = this.image_shape[0]-1};
        if(loc_y < 0){loc_y = 0};
        if(loc_y > this.image_shape[1]-1){loc_y = this.image_shape[1]-1};
        // return the right part of the face_grid
        var grid_position = 
        {   x: loc_x*div_x,
            y: loc_y*div_y,
            w: div_x,
            h: div_y,
        };
        return grid_position;
    }
}

// Point Class
function Point(x, y)
{   this.x = x;
    this.y = y;
}
