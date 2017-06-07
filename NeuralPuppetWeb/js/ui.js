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
    {   element.onchange = this.on_change;
        element.oninput = this.on_change;
    }

    Timeline.prototype.on_change = function()
    {   animation.go_to_frame(Math.round(element.value));
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

function PlayButton()
{   
    var element = document.getElementById('play-button');
    var play_button = this;

    this.on_click = function()
    {   animation.toggle_play();
        play_button.update_state();
    }

    PlayButton.prototype.update_state = function ()
    {
        if(animation.is_playing())
        {   element.innerText = 'PAUSE';
        }
        else
        {   element.innerText = 'PLAY!';
        }
    }

    PlayButton.prototype.setup = function ()
    {   
        element.onclick = this.on_click;
        this.update_state();
    }

    this.setup();
}

function SaveJsonButton()
{
    var element = document.getElementById('save-json-button');
    var button = this;

    this.on_context_click = function(e)
    {
        button.append_json();
    }

    this.on_click = function(e)
    {
        button.append_json();
    }

    SaveJsonButton.prototype.append_json = function()
    {   // Get current json data from Animation.data and append it to link element
        // Adapted from: http://stackoverflow.com/questions/19721439/download-json-object-as-a-file-from-browser
        let json = animation.get_json();
        var data = "text/json;charset=utf-8," + encodeURIComponent(json);
        element.href = 'data:' + data;
    }
    SaveJsonButton.prototype.setup = function ()
    {   
        element.onclick = this.on_click;
        element.oncontextmenu = this.on_context_click;    
    }

    this.setup();
}

function LoadJsonButton()
{
    var element = document.getElementById('load-json-button');
    var button = this;

    this.on_change = function(e)
    {   // From: https://developer.mozilla.org/en/docs/Using_files_from_web_applications
        animation.load_json(e.target.files);
    }

    LoadJsonButton.prototype.setup = function ()
    {   
        element.onchange = this.on_change;
    }

    this.setup();
}

function setup_ui()
{   // Prevent middle click pan from messing up window
    document.addEventListener ("click", function (e) 
    {   if (e.which === 2) 
        e.preventDefault();
    });

    var play_button = new PlayButton();
    var save_json_button = new SaveJsonButton();
    var load_json_button = new LoadJsonButton();
}


