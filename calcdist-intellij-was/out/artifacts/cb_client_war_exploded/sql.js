var localDatabase=window;
(function()
{
start();

})
()

function start(){
	var db = new alasql.Database();
	
	 alasql('ATTACH INDEXEDDB DATABASE nodedsDB; '+
     'USE nodedsDB; '+
     'SELECT * '+
     '       FROM nodes '
     , [], function (res) {
		 writeToConsoleScreen(res.pop());
}); 
	
	//db.exec("CREATE TABLE cities (city string, population number)");

	//db.exec("INSERT INTO cities VALUES ('Rome',2863223),('Paris',2249975),('Berlin',3517424),('Madrid',3041579)");

	//var res = db.exec("SELECT * FROM nodes");

	//writeToConsoleScreen(res);


	
}
