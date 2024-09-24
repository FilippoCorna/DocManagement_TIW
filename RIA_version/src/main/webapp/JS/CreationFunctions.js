function showWizardCreationFold(e){
	e.preventDefault();
	removeDoc();
	document.getElementById("show").className = "hidden";
	document.getElementById("foldCreation").className = "wizard";
	let form = document.getElementById("foldCreation");
	document.getElementById("foldCreationButton").addEventListener("click", createFolder(form));
	document.getElementById("foldCancelButton").addEventListener("click", cancel);
}


function createFolder(form1){
	let form = form1;
	
	function x (e){
		e.preventDefault();
		let mes = document.getElementById("foldCreationMessage");
		
		
	makeCall("POST", "CreateFolder", form, function(req){
		
		if(document.getElementById("name").value == null){
			mes.textContent = "Scegli un nome";
			return;
		}
		
		if (req.readyState == 4) {
		            var message = req.responseText;
		            if (req.status == 200) {
		              var folder = JSON.parse(req.responseText);
		              if (folder.length == 0) {
		                window.alert("Errore nel reperire la cartella creata");
		                return;
		              }
		              foldAllocation(folder);
		              canc(form);
		              
		             
		            
		          } else if (req.status == 403) {
	                  window.location.href = req.getResponseHeader("Location");
	                  window.sessionStorage.removeItem('username');
	                  } 
	                else if( req.status == 400){
						window.alert(message);
					}
					else if( req.status == 500){
						mes.textContent = message;
					}
					else if( req.status == 401){
						window.alert(message);
					}
	                  else {
		            window.alert(message);
		          }}
	}, false);
	};
	
	return x;
};


function foldAllocation(folder){
	let folderList = document.getElementById("folderList");
	let li = document.createElement("li");
	let div = document.createElement("div");
	let macrodiv = document.createElement("div");
	let button = document.createElement("button");
	button.addEventListener("click", showWizardCreationSub);
	button.className = "creation";
	button.textContent = "+";
	li.value = folder.id;
	div.textContent = folder.name;
	div.id = "fol"+ folder.id;
	button.id = "folBut"+ folder.id;
	macrodiv.appendChild(div);
	macrodiv.appendChild(button);
	li.appendChild(macrodiv);
	folderList.appendChild(li);
	
	let ul = document.createElement("ul");
	li.appendChild(ul);
	
}







function showWizardCreationSub(e){
	e.preventDefault();
	removeDoc();
	document.getElementById("show").className = "hidden";
	document.getElementById("subCreation").className = "wizard";
	let form = document.getElementById("subCreation");
	let father = e.target.closest("li").closest("li");
	document.getElementById("folderId").value = father.value;
	document.getElementById("folderFather").textContent = "Cartella padre: " + father.firstChild.firstChild.innerText;
	document.getElementById("subCreationButton").addEventListener("click", createSubfolder(form, father.firstChild.nextSibling));
	document.getElementById("subCancelButton").addEventListener("click", cancel);
}

function createSubfolder(form1, father1){
	let form = form1;
	let father = father1;
	
	function x (e){
		e.preventDefault();
		let mes = document.getElementById("subCreationMessage");
		
		
	makeCall("POST", "CreateSubfolder", form, function(req){
		if(document.getElementById("name").value == null){
			mes.textContent = "Scegli un nome";
			return;
		}
		
		if (req.readyState == 4) {
		            var message = req.responseText;
		            if (req.status == 200) {
		              var subfolder = JSON.parse(req.responseText);
		              if (subfolder.length == 0) {
		                window.alert("Errore nel reperire la sottocartella creata");
		                return;
		              }
		              subAllocation(subfolder,father);
		              canc(form);
		              
		             
		            
		          } else if (req.status == 403) {
	                  window.location.href = req.getResponseHeader("Location");
	                  window.sessionStorage.removeItem('username');
	                  } 
	                else if( req.status == 400){
						window.alert(message);
					}
					else if( req.status == 500){
						mes.textContent = message;
					}
					else if( req.status == 401){
						window.alert(message);
					}
	                  else {
		            window.alert(message);
		          }}
	}, false);
	};
	
	return x;
};


function subAllocation(subfolder, father){
	let docList = document.createElement("ul");
	docList.id = "docList" + subfolder.id;
	li = document.createElement("li");
	div = document.createElement("div");
	macrodiv = document.createElement("div");
	div.className = "clickable";
	button = document.createElement("button");
	button.className = "creation";
	button.textContent = "+";
	button.addEventListener("click", showWizardCreationDoc);
	li.value = subfolder.id;

	
	div.id ="sub"+subfolder.id;
	button.id = "subBut"+ subfolder.id;
	div.addEventListener("click", showDocuments(subfolder.id, subfolder.name));
	div.textContent = subfolder.name;
	div.addEventListener("dragover",dragOver);
	div.addEventListener("dragleave",dragLeave);
	div.addEventListener("drop",drop);
	macrodiv.appendChild(div);
	macrodiv.appendChild(button);
	li.appendChild(macrodiv);
	li.append(docList);
	father.appendChild(li);
}



function showWizardCreationDoc(e){
	e.preventDefault();
	removeDoc();
	document.getElementById("show").className = "hidden";
	document.getElementById("docCreation").className = "wizard";
	let form = document.getElementById("docCreation");
	let father = e.target.closest("li");
	document.getElementById("subfolderId").value = father.value;
	document.getElementById("subfolderFather").textContent = "Cartella padre: " + father.firstChild.firstChild.textContent;
	document.getElementById("docCreationButton").addEventListener("click", createDocument(form, father));
	document.getElementById("docCancelButton").addEventListener("click", cancel);
}



function createDocument(form1, father1){
	let form = form1;
	let father = father1;
	
	function x (e){
		e.preventDefault();
		let mes = document.getElementById("docCreationMessage");
		
		
	makeCall("POST", "CreateDocument", form, function(req){
		
		if(document.getElementById("docname").value == null){
			mes.textContent = "Scegli un nome";
			return;
		}
		
		if (req.readyState == 4) {
		            var message = req.responseText;
		            if (req.status == 200) {
		              var doc = JSON.parse(req.responseText);
		              if (doc.length == 0) {
		                window.alert("Errore nel reperire il documento creato");
		                return;
		              }
		              //docAllocation(doc,father);
		              window.alert("Documento creato");
		              canc(form);
		              return;
		              
		             
		            
		          } else if (req.status == 403) {
	                  window.location.href = req.getResponseHeader("Location");
	                  window.sessionStorage.removeItem('username');
	                  } 
	                else if( req.status == 400){
						window.alert(message);
					}
					else if( req.status == 500){
						mes.textContent = message;
					}
					else if( req.status == 401){
						window.alert(message);
					}
	                  else {
		            window.alert(message);
		          }}
	}, false);
	};
	
	return x;
};

/*
function docAllocation(doc, father){
	let li = document.createElement("li");
		li.className = "clickable";
		li.draggable = true;
		li.id ="doc"+ doc.id;
		li.textContent = doc.name ;
		li.addEventListener("click", showDoc(doc.id, father.value));
		let ul = father.firstChild.firstChild.firstChild.nextSibling;
		if(ul == null){
			ul = document.createElement("ul");
			ul.className = "document";
			father.firstChild.firstChild.append(ul);
		}
		ul.appendChild(li);
}

*/

function cancel(e){
	e.preventDefault();
	let button,form,div;
	button = document.getElementById("subCreationButton");
	if(button != null){
		form = button.closest("form");
		button.remove();
		button = document.createElement("button");
		button.type = "submit";
		button.textContent="sumbit";
		button.id = "subCreationButton";
		document.getElementById("subCreationMessage").textContent="";
		form.appendChild(button);
		form.appendChild(document.getElementById("subCancelButton"));
		form.appendChild(document.getElementById("subCreationMessage"));
		form.reset();
	}
	
	button = document.getElementById("foldCreationButton");
	if(button != null){
		form = button.closest("form");
		button.remove();
		button = document.createElement("button");
		button.type = "submit";
		button.id = "foldCreationButton";
		button.textContent="submit";
		document.getElementById("foldCreationMessage").textContent="";
		form.appendChild(button);
		form.appendChild(document.getElementById("foldCancelButton"));
		form.appendChild(document.getElementById("foldCreationMessage"));
		form.reset();
	}
	
	button = document.getElementById("docCreationButton");
	if(button != null){
		form = button.closest("form");
		button.remove();
		button = document.createElement("button");
		button.type = "submit";
		button.id = "docCreationButton";
		button.textContent="sumbit";
		document.getElementById("docCreationMessage").textContent="";
		form.appendChild(button);
		form.appendChild(document.getElementById("docCancelButton"));
		form.appendChild(document.getElementById("docCreationMessage"));
		form.reset();
	}
	
	
	button = document.getElementById("deleteButton");
	if(button != null){
		form = button.closest("form");
		button.remove();
		button = document.createElement("button");
		button.type = "submit";
		button.id = "deleteButton";
		button.textContent="sumbit";
		form.appendChild(button);
		form.appendChild(document.getElementById("delCancelButton"));
		form.reset();
		
	}
			
	
	document.getElementById("bin").src = "binOrange.png";
	e.target.closest("form").reset();
	e.target.closest("form").className= "hidden";
	document.getElementById("show").className = "";
}


function canc(formX){
	let button,form,div;
	button = document.getElementById("subCreationButton");
	if(button != null){
		form = button.closest("form");
		button.remove();
		button = document.createElement("button");
		button.type = "submit";
		button.textContent="submit";
		button.id = "subCreationButton";
		document.getElementById("subCreationMessage").textContent="";
		form.appendChild(button);
		form.appendChild(document.getElementById("subCancelButton"));
		form.appendChild(document.getElementById("subCreationMessage"));
		form.reset();
	}
	
	button = document.getElementById("docCreationButton");
	if(button != null){
		form = button.closest("form");
		button.remove();
		button = document.createElement("button");
		button.type = "submit";
		button.id = "docCreationButton";
		button.textContent="submit";
		document.getElementById("docCreationMessage").textContent="";
		form.appendChild(button);
		form.appendChild(document.getElementById("docCancelButton"));
		form.appendChild(document.getElementById("docCreationMessage"));
		form.reset();
	}
	
	
	button = document.getElementById("foldCreationButton");
	if(button != null){
		form = button.closest("form");
		button.remove();
		button = document.createElement("button");
		button.type = "submit";
		button.id = "foldCreationButton";
		button.textContent="submit";
		document.getElementById("foldCreationMessage").textContent="";
		form.appendChild(button);
		form.appendChild(document.getElementById("foldCancelButton"));
		form.appendChild(document.getElementById("foldCreationMessage"));
		form.reset();
	}
	
	
	button = document.getElementById("deleteButton");
	if(button != null){
		form = button.closest("form");
		button.remove();
		button = document.createElement("button");
		button.type = "submit";
		button.id = "deleteButton";
		button.textContent="sumbit";
		form.appendChild(button);
		form.appendChild(document.getElementById("delCancelButton"));
		form.reset();
		
	}
			
	
	formX.reset();
	formX.className= "hidden";
	document.getElementById("show").className = "";
}