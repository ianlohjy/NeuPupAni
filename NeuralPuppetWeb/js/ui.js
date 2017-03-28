function Timeline()
{
    var element = document.getElementById('timeline');

    // Timeline Data
    Object.defineProperty(this, 'value',
    {   get: function(){return element.value;},
        set: function(value){element.value = value;}
    });

    Object.defineProperty(this, 'max',
    {   get: function(){return element.max;},
        set: function(value){element.max = value;}
    });

    // Methods
    Timeline.prototype.setup = function()
    {
        element.onchange = this.on_change;
        element.oninput = this.on_change;
    }

    Timeline.prototype.on_change = function()
    {
        animation.go_to_frame(element.value);
    }

    Timeline.prototype.update_length = function()
    {   // Update's timeline length to reflect the current animation state
        if(animation.data.path.length > 0)
        {   this.max = animation.data.path.length - 1;
            this.value = animation.current_frame;
        }
    }
    this.setup();
}

function Play()
{   
    var element = document.getElementById('play-button');
    var play_button = this;

    this.on_click = function()
    {   animation.toggle_play();
        play_button.update_state();
    }

    Play.prototype.update_state = function ()
    {
        if(animation.is_playing())
        {   element.innerText = 'PAUSE';
        }
        else
        {   element.innerText = 'PLAY!';
        }
    }

    Play.prototype.setup = function ()
    {   
        element.onclick = this.on_click;
        this.update_state();
    }

    this.setup();
}