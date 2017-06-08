function Grid()
{
    var canvas  = new Canvasy('anim-grid'); // Canvasy
    var context = canvas.context; // Canvas context
    
    var mouse_down = function(event)
    {
        animation.start_recording
            (canvas.mouse_x/ canvas.width, 
             canvas.mouse_y/ canvas.height);
    }

    var mouse_up = function()
    {   animation.stop_recording();
    }

    // Initialization & Update
    Grid.prototype.setup = function()
    {
        canvas.setup_mouse_events();
        canvas.mouse_down = mouse_down;
        canvas.mouse_up = mouse_up;
    }

    Grid.prototype.update = function()
    {   // All runtime stuff goes here
        this.draw();
        this.draw_recording();
    }

    // Drawing
    Grid.prototype.draw = function()
    {   // Draws background elements of the grid
        context.clearRect(0, 0, canvas.width, canvas.height);  
        context.fillStyle = 'rgb(0,0,0)';
        context.fillRect(0, 0, canvas.width, canvas.height);
        context.globalAlpha = 0.25;
        context.drawImage(animation.data.image, 0, 0, canvas.width, canvas.height); 
        context.globalAlpha = 1;
    }

    Grid.prototype.draw_recording = function()
    {   // Draws path from animation.data
        if(animation.data.size > 0)
        {   let path = animation.data.path;   
            
            context.beginPath();
            context.strokeStyle = 'rgb(255,0,0)';
            context.lineCap = 'round';
            context.lineJoin = 'round';
            context.lineWidth = 5;

            for(var p=0; p<path.length-1; p++)
            {   //let stroke_colour = (p/path.length)*200.0;
                //console.log(p/path.length);
                //context.strokeStyle = 'rgb('+(stroke_colour+55)+','+0+','+0+')';
                
                context.beginPath();
                context.moveTo(path[p].x*canvas.width, path[p].y*canvas.height);
                context.lineTo(path[p+1].x*canvas.width, path[p+1].y*canvas.height);
                context.stroke(); 
                context.closePath();
            } 

            //context.stroke(); 
            //context.closePath();

            // Draw position of current cursor
            let cur_x = path[animation.current_frame].x * canvas.width;
            let cur_y = path[animation.current_frame].y * canvas.height;

            context.fillStyle = 'rgb(255,255,255)';
            context.beginPath();
            context.arc(cur_x, cur_y, 8, 0, Math.PI * 2, true); // Frame cursor
            context.fill();
        }
    }

    // Utilities
    Grid.prototype.get_context = function()
    {   return context;
    }

    Grid.prototype.get_canvas = function()
    {   return canvas;
    }

    // Grid properties
    Object.defineProperty(this, 'context',
    {   get: function(){return context},
    });

    Object.defineProperty(this, 'canvas',
    {   get: function(){return canvas},
    });

    this.setup();
}