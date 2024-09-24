

//Function give back the eventListener function (loadDocuments)
function showDocuments(sub, subName){
	let subfolder = sub;
	let name = subName;
	

	//doGet to server to get the documents and call createDocumentList
	function loadDocuments(e){
		e.preventDefault();
		let check = document.getElementById("sub"+sub);
		
		if(check.childNodes.length> 1)
			return; 
		
		let url = "Documents?subfolderId=" + subfolder +"&name=" + name;
		makeCall("GET",url,null,
		function(req){
			
			if (req.readyState == 4) {
		            var message = req.responseText;
		            if (req.status == 200) {
		              var documents = JSON.parse(req.responseText);
		              if (documents.length == 0) {
		                window.alert("Ancora nessun documento in questa sottocartella");
		                return;
		              }
		              removeDoc();
		              createDocumentList(documents, subfolder, name); 
		            
		          } else if (req.status == 403) {
	                  window.location.href = req.getResponseHeader("Location");
	                  window.sessionStorage.removeItem('username');
	                  } 
	                else if( req.status == 400){
						window.alert(message);
					}
					else if( req.status == 500){
						window.alert(message);
					}
					else if( req.status == 401){
						window.alert(message);
					}
	                  else {
		            window.alert(message);
		          }}
			
		},false);
	}

return loadDocuments;

}

//Create documents list
function createDocumentList(documents, subfolder){
	let i;
	let page = document.getElementById("sub" + subfolder);
		
	page.className = "reclick";
	let folderList = document.getElementById("folderList");
	//page.removeChild(folderList);
	let documentsList = document.getElementById("docList" + subfolder);
	documentsList.className = "document";
	documentsList.id = "documentsList";
	documentsList.value = subfolder;
	let doc;
	for(i = 0; i < documents.length; i++ ){
		let li = document.createElement("li");
		li.className = "clickable";
		li.draggable = true;
		doc = documents[i];
		li.id ="doc"+ doc.id;
		li.value = doc.id;
		li.textContent = doc.name ;
		li.addEventListener("click", showDoc(doc.id, subfolder));
		li.addEventListener("dragstart",dragStart);
		
		documentsList.appendChild(li);
	}
	
	//page.appendChild(documentsList);
	
}


function showDoc(docId, subId){
	this.docId = docId;
	this.subId = subId;
	
	function loadDoc(e){
		e.preventDefault();
		let check = document.getElementById("doc"+docId);
		
		if(check.childNodes.length> 1)
			return;
		
		let url = "ShowDocument?documentId=" + docId +"&subfolderId=" + subId ;
		makeCall("GET",url,null,
		function(req){
			
			if (req.readyState == 4) {
		            var message = req.responseText;
		            if (req.status == 200) {
		              var documentToShow = JSON.parse(req.responseText);
		              if (documentToShow.length == 0) {
		                window.alert("Errore, documento vuoto");
		                return;
		              }
		              //removeDoc();
		              createDoc(documentToShow);
		            
		          } else if (req.status == 403) {
	                  window.location.href = req.getResponseHeader("Location");
	                  window.sessionStorage.removeItem('username');
	                  } 
	                else if( req.status == 400){
						window.alert(message);
					}
					else if( req.status == 500){
						window.alert(message);
					}
					else if( req.status == 401){
						window.alert(message);
					}
	                  else {
		            window.alert(message);
		          }}
			
		},false);
	}

	
	return loadDoc;
}

//Remove doc of previous selected sub  and reset clickable previous selected sub
function removeDoc(){
	let docs =document.getElementById("documentsList");
	if(docs == null)
		return;
		
	let docListNew = document.createElement("ul");
	docListNew.id = "docList" + docs.value;
	docs.id = docListNew.id;
	let father = docs.parentElement;
	let sub = document.getElementsByClassName("reclick");
	while(sub.length > 0)
		sub[0].className = "clickable";
	if(docs != null){
		docs.remove();
		father.appendChild(docListNew);
						
	}
}

function createDoc(doc){
	let page = document.getElementById("doc" + doc.id);
	page.className = "reclick";
	let docx = document.createElement("table");
	let row = document.createElement("tr");
	let cell = document.createElement("td");
	cell.textContent = "Name";
	row.appendChild(cell);
	cell = document.createElement("td");
	cell.textContent = doc.name;
	row.appendChild(cell);
	docx.appendChild(row);
	
	row = document.createElement("tr");
	cell = document.createElement("td");
	cell.textContent = "Date";
	row.appendChild(cell);
	cell = document.createElement("td");
	cell.textContent = doc.creationDate;
	row.appendChild(cell);
	docx.appendChild(row);
	
	row = document.createElement("tr");
	cell = document.createElement("td");
	cell.textContent = "Type";
	row.appendChild(cell);
	cell = document.createElement("td");
	cell.textContent = doc.type;
	row.appendChild(cell);
	docx.appendChild(row);
	
	row = document.createElement("tr");
	cell = document.createElement("td");
	cell.textContent = "Summary";
	row.appendChild(cell);
	cell = document.createElement("td");
	cell.textContent = doc.summary;
	row.appendChild(cell);
	docx.appendChild(row);
	
	page.appendChild(docx);



}
