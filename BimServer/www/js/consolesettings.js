var ConsoleSettings = {
	title: "BIMserver API",
	subtitle: "Execute your own calls and get to learn the way of the BIMserver",
	getBaseUrl: function(){
		return "http://sandbox.bimserver.org"
	},
	getVersion: function(){
		return "1.2";
	}, getServiceInterfaces: function(consoleObject, callback){
		var request = {
			request: {
				interface: "org.bimserver.MetaInterface",
				method: "getAllAsJson"
			}
		};
		consoleObject.call(request, function(data){
			callback(JSON.parse(data.response.result).services);
		});
	}
}