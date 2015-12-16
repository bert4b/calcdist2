(function()
{
start();

})
();
var p2p={};
var guid=0;
var initiator=true;
var alreadySent=false;
var ws = new WebSocket("ws://localhost:9080/cb-server/socket.io");
var receivedSignalObject=null;
var connectionEstablishedIndicator=false;
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
	//writeToConsoleScreen(fetch);
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
	return guid;
}
function waitForAnswerFromSlave(restful){
    var resource = restful('http://localhost:9080/cb-server/rest/connect', fetchBackend(fetch));
    var signalEntity=resource.one('waitforanswer',generateGuid()).get().then(function(response) {
        writeToConsoleScreen("Initiator get answer, respond.");
        p2p.signal(JSON.parse(response.body().data().answer));
    });
}
function sendSignalToServer(signalData,restful){
	var resource = restful('http://localhost:9080/cb-server/rest/connect', fetchBackend(fetch));
	var signalEntity=resource.one('signalmsg',generateGuid()).get().then(function(response) {
		const dataEntity = response.body();
		const data=dataEntity.data();
        writeToConsoleScreen(data);
		data.signal=signalData;
		dataEntity.save().then(function(response){
			if (response.statusCode()==200){
				writeToConsoleScreen("Signal sent: response ok");

                //Wait while client gets signal and answers.
                waitForAnswerFromSlave(restful);


                //ws.onopen = function() {
                //    writeToConsoleScreen('WS open');
                //    var node = {"id":generateGuid(),"request":"companions"};
                //    ws.send(JSON.stringify(node));
                //};
                //ws.onclose = function() {
                //    //do something when connection close
                //    writeToConsoleScreen("WS closed");
                //};
                //ws.onmessage = function(message) {
                //    writeToConsoleScreen(message);
                //    writeToConsoleScreen(message.data);
                //    var nodeResponse=JSON.parse(message.data);
                //    if (nodeResponse.nodeSignal!="" && nodeResponse.companionWith!=generateGuid()){
                //        p2p.signal(nodeResponse.nodeSignal);
                //    }else{
                //        writeToConsoleScreen("no companion found");
                //    }
                //};
			}
		});
	});

}

function receiveSignalFromServer(restful){
    var resource = restful('http://localhost:9080/cb-server/rest/connect', fetchBackend(fetch));
    resource.one('receivesignalmsg',generateGuid()).get().then(function(response) {
            const data = response.body().data();
            writeToConsoleScreen(data);
            if (response.statusCode()==200){
                writeToConsoleScreen("Received signal");
                receivedSignalObject=response.body().data();
                p2p.signal(data.signal);
            }

    });
}
function sendAnswerBackToInitiator(restful,answerdata,dataObject){
    var resource = restful('http://localhost:9080/cb-server/rest/connect', fetchBackend(fetch));

    //var signalEntity=resource.one('answersignalmsg',generateGuid()).get().then(function(response) {


        //const dataEntity = response.body();
        //const data=dataEntity.data();
        //writeToConsoleScreen(data);
        //data.answer=answerdata;
        //dataEntity.save().then(function(response){
        //    if (response.statusCode()==200){
        //        writeToConsoleScreen("Answer signal");
        //    }
        //});
    //});
    var signalEntity=resource.one('answersignalmsg',generateGuid());

    dataObject.answer=answerdata
    signalEntity.put(dataObject).then(function(response) {
        writeToConsoleScreen(response);
        if (response.statusCode()==200) {

                 writeToConsoleScreen("Answered signal");
        }
    });
}
function setupP2P(simplepeer,data,restful){
    var datObject=data;
    initiator=datObject.initiator;
	var p = new simplepeer({ initiator: datObject.initiator, trickle: false,stream:false});
	p2p=p;
	p.on('error', function (err) { writeToConsoleScreen('Error: '+ err) });
    if (datObject.initiator!=true) {
        //receive signal from server
        writeToConsoleScreen("I'm slave: Receive signal from server");
        receiveSignalFromServer(restful);
    }
	p.on('signal', function (data) {
        document.querySelector("#offer").value = JSON.stringify(data);
        if (initiator==true) {
            if (alreadySent==false){
                //send signal to server
                writeToConsoleScreen("I'm initiator: Send signal to server");
                sendSignalToServer(JSON.stringify(data), restful);
            }else{
                //Send not once again signal to server
                alreadySent=true;
            }
        }
        else{
          writeToConsoleScreen("I get answer from initiator.");
          sendAnswerBackToInitiator(restful,JSON.stringify(data),receivedSignalObject);
        }
	});
	p.on('connect', function () {
		writeToConsoleScreen("Connection established!");
        connectionEstablished();
	});

	p.on('data', function (data) {
        writeToConsoleScreen(data);
		document.querySelector("#received").innerHTML=document.querySelector("#received").innerHTML+data;
        var jobTask=data;
        if (jobTask.nodeid!=generateGuid()){
            writeToConsoleScreen("Executing");
            //Execute job
            var result=eval(jobTask.jobExecutable);
            //Send result back
            if (result!=null || result!=undefined){
                sendResponseAfterExecutingJob(jobTask,result);
            }
        }

	})
}

function sendResponseAfterExecutingJob(job,result){

    var resultResponse = {"nodeid":generateGuid(),"jobid":+ job.jobid, "result":result};
    writeToConsoleScreen("Send result back");
    p2p.send(JSON.stringify(resultResponse));
}

function fireJob(){
    var id=1;
    var job=document.querySelector('#job').value;
    var jobTask = {"nodeid":generateGuid(),"jobid":+ id, "jobExecutable":job};
    p2p.send(JSON.stringify(jobTask));
}
function connectionEstablished(){
    document.querySelector("#connection").innerHTML="Connection Established";
    connectionEstablishedIndicator=true;
}

function start(){

	require(['node_modules/restful.js/dist/restful.standalone.js','node_modules/socket.io-p2p/socketiop2p.min.js','node_modules/socket.io/node_modules/socket.io-client/socket.io.js','node_modules/simple-peer/simplepeer.min.js'],function(restful,socketiop2p,socketioclient,simplepeer){

        //var socketio=socketioclient('http://localhost:9080/cb-server/rest/info');
        //socketio.on('clientcount', function (data) {
        //    writeToConsoleScreen(data);
        //});
        var resource = restful('http://localhost:9080/cb-server/rest/connect', fetchBackend(fetch));
        //resource.custom('dsinfo').get().then(function(response){
			//writeToConsoleScreen(response);
			//  const article = response.body().data();
			//	writeToConsoleScreen(article);
        //});
    if (!connectionEstablishedIndicator) {
        resource.one('login', generateGuid()).get().then(function (response) {

            writeToConsoleScreen("ClientID: " + generateGuid());
            const data = response.body().data();
            writeToConsoleScreen("Login");
            if (response.statusCode() == 200) {
                setupP2P(simplepeer, data, restful);
            }

        });
    }

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
        });
        p2p.on('go-private', function () {
            p2p.upgrade(); // upgrade to peerConnection
        });


        socket.on('connection', function(socket) {
            socket.on('peer-msg', function(data) {
                console.log('Message from peer: %s', data);
                socket.broadcast.emit('peer-msg', data);
            });

            socket.on('go-private', function(data) {
                socket.broadcast.emit('go-private', data);
            });
        });




    });

}

}
