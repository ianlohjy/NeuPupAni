
function JSONReader()
{   // Simple json reader util, requires FileReader support 
    this.file_reader = new FileReader();
    this.json = null; // Where JSON result is stored if loaded

    // File Reading
    JSONReader.prototype.setup = function()
    {
    }   

    JSONReader.prototype.read_json = function(file, callback)
    {   // Read from file list, and do something with JSON result 
        // https://developer.mozilla.org/en/docs/Web/API/FileReader
        // http://jsfiddle.net/zTe4j/58/
        
        /*
        if(this.file_reader.readyState == 1)
        {   console.log('ABORTING CURRENT READ');
            this.file_reader.abort();
        }
        //console.log(typeof file_url);
        //console.log(file_url);
        */

        //this.file_reader.readAsText(file_url[0]);


        console.log(file[0]);

        this.file_reader = new FileReader();
        this.file_reader.onload = (function(loaded_event)
        {
            //console.log(loaded_event.target.result);
            try{
                this.json = JSON.parse(loaded_event.target.result);
                callback(this.json);
            }
            catch(exception){
                this.json = null;
            }
        });
        this.file_reader.readAsText(file);
    }


    /*
    SimpleReader.prototype.get_result = function()
    {   // Returns read result, if available
        if(this.file_reader.readyState == 2)
        {   return this.file_reader.result;
        }
        else
        {   return null;
        }
    }

    
    // Events + Callbacks
    SimpleReader.prototype.on_read_fail = function(callback)
    {   // Sets callback for onabort and onerror
        this.file_reader.onabort = callback;
        this.file_reader.onerror = callback;
    }

    SimpleReader.prototype.on_read_done = function(callback)
    {   // Sets callback for onloadend
        this.file_reader.onloadend = callback;
    }*/
}