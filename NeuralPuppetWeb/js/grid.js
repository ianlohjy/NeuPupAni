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
        context.globalAlpha = 0.25;
        //context.drawImage(animation.data.image, 0, 0, canvas.width, canvas.height); 
        context.globalAlpha = 1;
    }

    Grid.prototype.draw_recording = function()
    {   // Draws path from animation.data
        if(animation.data.size > 0)
        {   let path = animation.data.path;   
            
            context.beginPath();
            context.strokeStyle = 'red';
            context.lineJoin = 'round';
            context.lineWidth = 2;

            context.moveTo(path[0].x*canvas.width, path[0].y*canvas.height);

            for(var p=0; p<path.length; p++)
            {   context.lineTo(path[p].x*canvas.width, path[p].y*canvas.height);
            } 

            context.stroke(); 
            context.closePath();
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