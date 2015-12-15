    var ws = createWS();



    function createWS(){
        return new WebSocket("ws://localhost:9080/cb-server/socketconfig.io");
    }

    function retry(){
        ws = createWS();
    }

    ws.onopen = function() {
        console.info('open');

        ws.send('test');
    };


    ws.onclose = function() {
        //do something when connection close
        console.info("ws closed");
        setInterval(retry(),1000);
    };


    ws.onerror = function(err){
        console.info("Error:"+err);
        retry();
    };

    ws.onmessage = function(message) {
        console.info(message);
        console.info(message.data);


    };

    function reset(){
        ws.send('reset');
        console.info("reset")
    }