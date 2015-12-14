(function()
{
start();

})
()
var p2p={};
var guid=0;
var configWebRTC={
		  initiator: location.hash === '#1',
		  channelConfig: {},
		  channelName: 'bert',
		  config: { iceServers: [ { url: 'stun:23.21.150.121' } ] },
		  constraints: {},
		  reconnectTimer: 0,
		  sdpTransform: function (sdp) { return sdp },
		  stream: false,
		  trickle: true
		};
function fetchBackend(fetch){
	writeToConsoleScreen(fetch);
}

function offer(){
	 p2p.signal(JSON.parse(document.querySelector('#offer').value))

}
function send(){

	 p2p.send(document.querySelector('#send').value);

}

function getMilSeconds(){
	  var d = new Date();
	    var n = d.getMilliseconds();

	return n;
}
function generateGuid(){
	if (guid==null || guid==0 || guid==undefined){
        //Should hapen only once.
		guid= getMilSeconds();
	}
    writeToConsoleScreen(guid);
	return guid;
}
function sendSignalToServer(signalData,restful){
	var resource = restful('http://localhost:9080/cb-server/rest/connect', fetchBackend(fetch));






	var signalEntity=resource.one('signalmsg',generateGuid()).get().then(function(response) {

		const dataEntity = response.body();
		const data=dataEntity.data();
		data.signal=signalData;
		dataEntity.save().then(function(response){


			if (response.statusCode()==200){
				writeToConsoleScreen(response.body().data());

                var ws = new WebSocket("ws://localhost:9080/cb-server/socket.io");



                ws.onopen = function() {
                    writeToConsoleScreen('open');
                    var node = {"id":generateGuid(),"request":"companions"};
                    ws.send(JSON.stringify(node));
                };


                ws.onclose = function() {
                    //do something when connection close
                    writeToConsoleScreen("ws closed");
                };




                ws.onmessage = function(message) {
                    writeToConsoleScreen(message);
                    writeToConsoleScreen(message.data);
                    var nodeResponse=JSON.parse(message.data);
                    if (nodeResponse.nodeSignal!="" && nodeResponse.companionWith!=generateGuid()){
                        p2p.signal(nodeResponse.nodeSignal);
                    }else{
                        writeToConsoleScreen("no");
                    }

                };
			}
		});



	});

}

function setupP2P(simplepeer,data,restful){
	writeToConsoleScreen('p2p');
	writeToConsoleScreen(data.initiator);

	var p = new simplepeer({ initiator: data.initiator, trickle: false,stream:false});
	p2p=p;
	p.on('error', function (err) { console.log('error', err) })

	p.on('signal', function (data) {
		writeToConsoleScreen(JSON.stringify(data));
	  document.querySelector("#offer").value = JSON.stringify(data);
	  sendSignalToServer(JSON.stringify(data),restful);
	})



	p.on('connect', function () {
		writeToConsoleScreen('CONNECT');
		writeToConsoleScreen(p);
		p.send('test');
	})

	p.on('data', function (data) {
		document.querySelector("#received").innerHTML=document.querySelector("#received").innerHTML+data;

	})
}

function start(){

	require(['node_modules/restful.js/dist/restful.standalone.js','node_modules/socket.io-p2p/socketiop2p.min.js','node_modules/socket.io/node_modules/socket.io-client/socket.io.js','node_modules/simple-peer/simplepeer.min.js'],function(restful,socketiop2p,socketioclient,simplepeer){

        //var socketio=socketioclient('http://localhost:9080/cb-server/rest/info');
        //socketio.on('clientcount', function (data) {
        //    writeToConsoleScreen(data);
        //});
		var resource = restful('http://localhost:9080/cb-server/rest/connect', fetchBackend(fetch));
		resource.custom('dsinfo').get().then(function(response){
			writeToConsoleScreen(response);
			  const article = response.body().data();
				writeToConsoleScreen(article);
		});

		resource.one('login',generateGuid()).get().then(function(response) {

			writeToConsoleScreen(response.statusCode());
			const data = response.body().data();
			writeToConsoleScreen(data);
			if (response.statusCode()==200){
				setupP2P(simplepeer,data,restful);
			}

		});


		/*
		var ws = new WebSocket("ws://localhost:9080/cb-server/socket.io");

		ws.onopen = function() {
			writeToConsoleScreen('open');
			ws.send('test');
		};

		ws.onmessage = function(message) {
		 //do something when message arrives
		};

		ws.onclose = function() {
		 //do something when connection close
		};
         */



/*
		var resource = restful('http://localhost:9080/cb-server/rest/connect', fetchBackend(fetch));
		resource.custom('dsinfo').get().then(function(response){
			writeToConsoleScreen(response);
			  const article = response.body().data();
				writeToConsoleScreen(article);
		});
		resource.one('login',1).get().then(function(response) {
			writeToConsoleScreen(response);


			writeToConsoleScreen(response.body());

		  const article = response.body().data();
			writeToConsoleScreen(article);

		});
		*/

	});


function socketiop2p(){
    require(['node_modules/restful.js/dist/restful.standalone.js','node_modules/socket.io-p2p/socketiop2p.min.js','node_modules/socket.io/node_modules/socket.io-client/socket.io.js','node_modules/simple-peer/simplepeer.min.js'],function(restful,socketiop2p,socketioclient,simplepeer){



        var socket = socketioclient();
        socket.io.opts.path='/cb-server/socket.io';
        writeToConsoleScreen(socket);
        var opts = {autoUpgrade: false, peerOpts: {numClients: 10}};
        var p2p = new socketiop2p(socket,opts,function(){
            writeToConsoleScreen('p2p');
        });
        p2p.on('peer-msg', function (data) {
            console.log('From a peer %s', data);
        });
        p2p.on('ready', function(){
            writeToConsoleScreen('ready');
            p2p.usePeerConnection = true;
            p2p.emit('peer-obj', { peerId: peerId });
        })
        p2p.on('go-private', function () {
            p2p.upgrade(); // upgrade to peerConnection
        });


        socket.on('connection', function(socket) {
            socket.on('peer-msg', function(data) {
                console.log('Message from peer: %s', data);
                socket.broadcast.emit('peer-msg', data);
            })

            socket.on('go-private', function(data) {
                socket.broadcast.emit('go-private', data);
            });
        });




    });

}

}
