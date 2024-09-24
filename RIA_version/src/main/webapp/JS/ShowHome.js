(function(){
	
	let pageOrchestrator = new PageOrchestrator(); // main controller
	 window.addEventListener("load", () => {
		
	    if (sessionStorage.getItem("username") == null) {
	      window.location.href = "PublicPage.html";
	    } else {
	      pageOrchestrator.start(); // initialize the components
	      //pageOrchestrator.refresh();
	    } // display initial content
	  }, false);
	  
	  
	  let startElement; //for the move function
	  let startSubfold; //for the move function


	function loadContent(){

		makeCall("GET", "ShowHomePage", null, function(req){
			
			if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var folders = JSON.parse(req.responseText);
	              if (folders.length == 0) {
	                window.alert("Ancora nessuna cartella");
	                return;
	              }
	              createFolderList(folders); 
	              
	            
	          } else if (req.status == 403) {
                  window.location.href = req.getResponseHeader("Location");
                  window.sessionStorage.removeItem('username');
                  }
                  else {
	            window.alert(message);
	          }}
	        
			
			
		},false);
	}

	//Show username
	function personalMessage(){
		let name = document.getElementById("username");
		name.textContent = sessionStorage.getItem("username");
	}
	
	//With list of folders, got by json, create the <ul> list
	function createFolderList(folders){
		let folderList = document.getElementById("folderList");
		
		
		let folder, subfolder, subfolders, i = 0, j = 0;
		while(i < folders.length){
			let li = document.createElement("li");
			let div = document.createElement("div");
			let macrodiv = document.createElement("div");
			let button = document.createElement("button");
			button.addEventListener("click", showWizardCreationSub);
			button.className = "creation";
			button.textContent = "+";
			folder = folders[i];
			i++;
			li.value = folder.id;
			div.textContent = folder.name;
			div.id = "fol"+ folder.id;
			button.id = "folBut"+ folder.id;
			macrodiv.appendChild(div);
			macrodiv.appendChild(button);
			li.appendChild(macrodiv);
			folderList.appendChild(li);
			subfolders = folder.subfolders;
			
			//Append the subfolder list as a <ul> child of main <ul> list
			if(subfolders != null &&  subfolders.length != 0){
				
				let ul = document.createElement("ul");
				li.appendChild(ul);
				j = 0;
				while(j < subfolders.length){
					
					let docList = document.createElement("ul");
					
					
					li = document.createElement("li");
					div = document.createElement("div");
					macrodiv = document.createElement("div");
					div.className = "clickable";
					button = document.createElement("button");
					button.addEventListener("click", showWizardCreationDoc);
					button.className = "creation";
					button.textContent = "+";
			
					subfolder = subfolders[j];
					j++;
					
					docList.id = "docList" + subfolder.id;
					li.value = subfolder.id;
					div.id ="sub"+subfolder.id;
					button.id = "subBut"+ subfolder.id;
					div.addEventListener("click", showDocuments(subfolder.id, subfolder.name));
					div.textContent = subfolder.name;
					macrodiv.appendChild(div);
					macrodiv.appendChild(button);
					li.appendChild(macrodiv);
					li.append(docList);
					div.addEventListener("dragover",dragOver);
					div.addEventListener("dragleave",dragLeave);
					div.addEventListener("drop",drop);
					ul.appendChild(li);
				}
			}
			else{
				let ul = document.createElement("ul");
				li.appendChild(ul);
			}
		}
	}
	
	
	function PageOrchestrator() {
		
		this.start = function (){
			personalMessage();
			loadContent();
			document.getElementById("createfold").addEventListener("click",showWizardCreationFold);
			document.getElementById("bin").addEventListener("drop", showDelWizard);
	        document.getElementById("bin").addEventListener("dragover", delOver);
	        document.getElementById("bin").addEventListener("dragleave", delLeave);
		};
	}


})();