function handleTemplateSelect(evt) {
    var file = evt.target.files[0];
    var reader = new FileReader();
    reader.onload = function(e){
        $('#template').text(e.target.result);
    };
    reader.readAsText(file);
    $('#outTemplate').collapse();
}

function handleDataSelect(evt) {
    var file = evt.target.files[0];
    var reader = new FileReader();
    reader.onload = function(e){
        $.csv.toObjects(e.target.result,{separator:";"}, function(err,data){
            $('#outData').collapse();
            $('#variablesBadge').text(Object.keys(data[0]).length);
            for(variable in data[0]){
                $('#variablesList').append('<li><b>' + variable + "<b></li>");
            }
            $('#recordsBadge').text(data.length);
            for(var i=0; i<data.length;i++){
                var record = data[i];
                var attrList = "";
                for(var variable in record){
                    attrList += '<li><b>' + variable + '</b>:' + record[variable] + '</li>'; 
                }
                $('#recordsList').append('<li>Record ' + i + '<ul>' + attrList + '</ul></li>');
            }
        });
        $('#data').text(e.target.result);
    };
    reader.readAsText(file);
}

$(document).ready(function(){
    document.getElementById("templateFile").addEventListener('change', handleTemplateSelect, false);
    document.getElementById("dataFile").addEventListener('change', handleDataSelect, false);
    
    $(".toggler").click(function(){
        $(this).toggleClass('glyphicon-chevron-down glyphicon-chevron-up');
    });
    
    $("#submitbtn").click(function(){
    	console.log($('#template').val());
    	console.log($('#data').val());
        var parameters = {
                         	template : $('#template').val(),
                            csv : $('#data').val()
                         };
        $.post('queuejob', parameters, function(data){
        	alert("Job submitted");
            $('#submitbtn').hide();
        });
    });
});


