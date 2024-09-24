(function(){
	
	window.addEventListener("load", ()=>{
		var login = document.getElementById("loginButton");
		var registration = document.getElementById("registrationButton");
		login.addEventListener("click", loginFunc());
		registration.addEventListener("click", checkReg);
	
	}, false);
	
	
	
	//LOGIN FUNCTION
	/**
	Check Validity of the form.
	If wrong report the error.
	If all right doPost to server to check credentials.
	If username and password match with a user, save this in the server session 
	and his username in client session.
	Window show Home.html.
	If any error occurs show the errorin loginMessage.
	 */
	function loginFunc(){
		attempts = 3;
		
	function clickLog(event){
		let  form;
		event.preventDefault();
		form = event.target.closest("form");
		if(form.checkValidity()){
			makeCall("POST", 'Login', event.target.closest("form"),
       	  function(x) {
          if (x.readyState == XMLHttpRequest.DONE) {
            var message = x.responseText;
            switch (x.status) {
              case 200:
            	sessionStorage.setItem('username', message);
                window.location.href = "Home.html";
                break;
              case 400: // bad request
                document.getElementById("loginMessage").textContent = message;
                document.getElementById("loginMessage").className="Error";
                break;
              case 401: // unauthorized
                document.getElementById("loginMessage").textContent = message;
                document.getElementById("loginMessage").className="Error";
                break;
              case 500: // server error
            	document.getElementById("loginMessage").textContent = message;
            	document.getElementById("loginMessage").className="Error";
                break;
            }
          }
        }
      , true);
				
			}
			else
				form.reportValidity();
			
		}
		
		return clickLog;
	}
	
	//REGISTRATION FUNCTION
	/**
	Check form validity (if inputs are not null).
	Check if email is not bad formatted.
	Check if password and repeat password are equals.
	If something goes wrong show error message and return.
	Else doPost to server to make registration.
	 */
	function checkReg(event){  
		let password1, password2, email, message, form;
		event.preventDefault();
		form = document.getElementById("registration");
		password1 = document.getElementById("password1R");
		password2 = document.getElementById("password2R");
	    email = document.getElementById("emailR");
	    message = document.getElementById("registrationMessage");
	    
	    message.innerText = "";
	    message.className = "message";

		if(!form.checkValidity()){
			message.innerText = "Errore nei dati";
	    	message.className = "Error";
	    	return;
		}
	   
	    
		 if(! email.value
	    .toLowerCase()
	    .match(
	      /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
	    )){
		message.innerText = "E-mail non corretta";
	    message.className = "Error";
	    return;
		}
		
	
	
		let passwordString = password1.value;
		let password2String = password2.value;
			if(passwordString != password2String){
				message.innerText ="Le password non coincidono";
			    message.className = "Error";
			    return;
			}
			
		makeCall("POST", 'Registration', form,
        function(x) {
          if (x.readyState == XMLHttpRequest.DONE) {
            var message = x.responseText;
            switch (x.status) {
              case 200:
            	document.getElementById("registrationMessage").textContent=message;
            	document.getElementById("registration").reset();
                break;
              case 400: // bad request
                document.getElementById("registrationMessage").textContent = message;
                document.getElementById("registrationMessage").className="Error";
                break;
              case 500: // server error
            	document.getElementById("registrationMessage").textContent = message;
            	document.getElementById("registrationMessage").className="Error";
                break;
            }
          }
        }
      , false);
 		
	}
	
})();