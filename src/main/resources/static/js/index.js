$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	console.log("触发");
	console.log(title);
	console.log(content);

	$.post(
		CONTEXT_PATH+"/discuss/add",

		{"title":title , "content":content},

		function(data){
			data=$.parseJSON(data);

			console.log(data)

			$("#hintBody").text(data.msg);
			$("#hintModal").modal("show");

			setTimeout(function(){
				$("#hintModal").modal("hide");
			}, 2000);

		}
	);
}