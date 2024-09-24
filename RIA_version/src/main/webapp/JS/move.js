

	function dragStart(event) {
        startElement = event.target.closest("li");
        startElement.className = "startElement";
      	startSubfold = startElement.parentElement.parentElement;//li->ul->li
    }
    
    function dragOver(event) {
	var dest = event.target.closest("div");
	if(dest != startSubfold.firstChild.firstChild){
    	dest.className = "selected";
    	event.preventDefault(); 
    	}
    }
    
    function dragLeave(event) {
        // We need to select the row that triggered this event to marked as "notselected" so it's clear for the user 
        var dest = event.target.closest("div");

        // Mark  the current element as "notselected", then with CSS we will put it in black
        dest.className = "clickable";
    }
    
    
    function drop(event) {
        
        // Obtain the row on which we're dropping the dragged element
        var dest = event.target.closest("div");
        let subId = dest.closest("li").value;
        let docId = startElement.value;
        dest.className = "clickable";
        startElement.className = "clickable";
		removeDoc();
		
		makeCall("GET", "ChooseDestination?subfolderId="+subId+"&documentId="+docId, null, function(req){
			
			if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	                window.alert(message);
	                return;
	              
	          } else if (req.status == 403) {
                  window.location.href = req.getResponseHeader("Location");
                  window.sessionStorage.removeItem('username');
                  }
                  else {
	            window.alert(message);
	          }}
	        
			
			
		},false);
	
    }
    