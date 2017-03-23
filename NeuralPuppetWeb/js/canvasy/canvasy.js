function Canvasy (element_id) 
{   // Wrapper for Canvas context with some handy methods
    // Element Data
    this.id_tag  = element_id;
    this.id      = null;
    this.context = null;

    // Keep a reference to the parent class
    var _canvas = this;

    // Mouse Data
    this.mouse_x = 0;
    this.mouse_y = 0;
    // Mouse Event Methods (Change these to your own mouse event callbacks)
    this.mouse_up    = this.mouse_down  = this.mouse_wheel =
    this.mouse_enter = this.mouse_leave = this.mouse_out   =
    this.mouse_move  = this.mouse_over  = function(event){}; // Compressing space
    
    // Canvas Data
    Object.defineProperty(this, 'width',
    {   get: function(){return this.id.width;},
        set: function(value){this.id.width = value;}
    });

    Object.defineProperty(this, 'height',
    {   get: function(){return this.id.height;},
        set: function(value){this.id.height = value;}
    });
    
    // Prototype Methods
    Canvasy.prototype.init = function()
    {   // This method is run when the object is created.
        this.id = document.getElementById(this.id_tag); 
        this.setup_context();
        //this.setup_mouse_events(); // Don't run this by default, since it may be expensive
    }

    Canvasy.prototype.setup_context = function()
    {   // Gets and returns the 2d context if available
        this.context = this.id.getContext('2d');
        return this.context;
    }

    Canvasy.prototype.setup_mouse_events = function()
    {   // Register and set mouse events to log_mouse method
        this.id.onmouseup    = this.log_mouse;
        this.id.onmousedown  = this.log_mouse;  
        this.id.onmousewheel = this.log_mouse;
        this.id.onmouseenter = this.log_mouse;
        this.id.onmouseleave = this.log_mouse;
        this.id.onmouseout   = this.log_mouse;
        this.id.onmousemove  = this.log_mouse;
        this.id.onmouseover  = this.log_mouse;
    }

    Canvasy.prototype.log_mouse = function(event)
    {   /* In this scope, 'this' refers to the HTML element 
           returned by the mouse event NOT the parent class.
           This means we cannot use 'this' to access the class.
           Setting var _canvas = this; in the constructor 
           works around this by exposing the parent class.
        */ 
        let element_rect = _canvas.id.getBoundingClientRect();
        let x_offset = element_rect.left + window.pageXOffset;
        let y_offset = element_rect.top  + window.pageYOffset;
        
        _canvas.mouse_x = event.pageX - x_offset; 
        _canvas.mouse_y = event.pageY - y_offset; 

        switch (event.type)
        {   case 'mouseup':
                _canvas.mouse_up(event);
                break;

            case 'mousedown':
                _canvas.mouse_down(event);
                break;

            case 'mousewheel':
                _canvas.mouse_wheel(event);
                break;

            case 'mouseenter':
                _canvas.mouse_enter(event);
                break;

            case 'mouseleave':
                _canvas.mouse_leave(event);
                break;

            case 'mouseout':
                _canvas.mouse_out(event);
                break;

            case 'mousemove':
                _canvas.mouse_move(event);
                break;

            case 'mouseover':
                _canvas.mouse_over(event);
                break;
        }
    }

    Canvasy.prototype.todo = function()
    {   console.log('Fold Canvasy methods and properties into the Canvas prototype (?)');
    }

    // The prototypes are being set in a function so the class
    // can only be initialised at the end.
    this.init();
};


