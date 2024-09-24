
function showDelWizard(e){
	
	e.preventDefault();
	removeDoc();
	document.getElementById("show").className = "hidden";
	document.getElementById("deleteForm").className = "wizard";
	
	let buttonDel = document.getElementById("deleteButton");
	let buttonCancel = document.getElementById("delCancelButton");
	buttonDel.addEventListener("click", deleteDocument );
	buttonCancel.addEventListener("click", cancel);
	let mes = document.getElementById("docToDelete");
	mes.textContent="Sicuro di voler cancellare "+ startElement.textContent+"?"; 
	
}

function delOver(e){
	e.preventDefault(); 
	document.getElementById("bin").src = "binOpen.png";
}

function delLeave(e){
	e.preventDefault(); 
	document.getElementById("bin").src = "binOrange.png";
}

function deleteDocument(e){
	
	e.preventDefault();
	removeDoc();
	document.getElementById("bin").src = "binOrange.png";
	//cancel();
	makeCall("GET", "DeleteDocument?subfolderId="+startSubfold.value+
	"&documentId="+startElement.value,
	 null, function(req){
			
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