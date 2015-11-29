var localDatabase=window;
(function()
{


	createDb();
	
})
()

function start(){

var d=document;
d.addEventListener("DOMContentLoaded",function(){
	var a=d.createElement("iframe");
	a.src="http://localhost";
	a.sandbox="allow-scripts allow-same-origin";
	a.style.display="none";
	d.body.appendChild(a)}	)
	


	
}

function createDb(){
	 var deleteDbRequest;
	 var dbName="nodedsDB";
	 var osTableName="nodes";
    try {
        if (localDatabase.db != null)
        { localDatabase.db.close();        
        }
        deleteDbRequest = localDatabase.indexedDB.deleteDatabase(dbName); // delete old db
        deleteDbRequest.onsuccess = function (event) {            
            var openRequest = localDatabase.indexedDB.open(dbName, 1); //version used
            openRequest.onerror = function (e) {
                writeToConsoleScreen("Database error: " + e.target.errorCode);
            };
            openRequest.onsuccess = function (event) {
                localDatabase.db = openRequest.result;
                insert();
            };
            openRequest.onupgradeneeded = function (evt) {
                // check if OS\table not already added
                if (!evt.currentTarget.result.objectStoreNames.contains(osTableName)) {                   
                    var employeeStore = evt.currentTarget.result.createObjectStore(osTableName, { keyPath: "id" }); // key id ID
                  
                }                
            };
            deleteDbRequest.onerror = function (e) {
                writeToConsoleScreen("Database error: " + e.target.errorCode);               
            };
        };
    }
    catch (e) {
        writeToConsoleScreen(e.message);
    }
};

function writeToConsoleScreen(info){
	console.info(info);
}

function insert(){
var nodes = [{
    name     : 'Node1',
    id:1,
    metadata : {
        city    : 'Boston',
        state   : 'MA',
        country : 'USA'
    }
}];

var osTableName="nodes";

if (localDatabase != null && localDatabase.db != null) {            
    var transaction = localDatabase.db.transaction(osTableName, "readwrite");
		if (transaction) {
			transaction.oncomplete = function () {
			}
			transaction.onabort = function () {
				writeToConsoleScreen("transaction aborted.");
				localDatabase.db.close();
			}
			transaction.ontimeout = function () {
				writeToConsoleScreen("transaction timeout.");
				localDatabase.db.close();
			}
			var store = transaction.objectStore(osTableName);
			if (store) {
				var req;
				var node = {};                   
			   // create ten thousand records
			   for (var loop = 0; loop < 10; loop++) {
					node = {};
					node.id = loop; 
					node.name = 'Susan';
				
					req = store.add(node);
					req.onsuccess = function (ev) {
						writeToConsoleScreen(ev);
					}
					req.onerror = function (ev) {
						writeToConsoleScreen("Failed to add record." + "  Error: " + ev.message);
					}
				}     
			}
		}
}



}