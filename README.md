# DocManagement_TIW
Web application for document management

A single analysis of the project specifications was chosen, as we considered that the extension of these in the RIA version is more of an implementation rather than a functional matter.
For a more detailed discussion, please refer to the IFML diagrams of the two projects.

The class diagram consists of the bean and DAO packages, as we deemed it redundant to have a diagram for the controllers package, given that we already have the IFML diagrams.
The class diagram is also singular, as the elements shown are common to both projects.

In the database context, we have included, in addition to the ER schema, some SQL documents as a guide to the main queries and statements used in the development of this project.

The DOCUMENT entity has been interpreted as a company document; thus, the document type will take values such as: Financial Statement, Report, and so on.

Each servlet performs checks on both the well-formatting of parameters, ensuring it receives non-null and correctly typed values, and on the validity of these values.
Once the user is identified and authenticated through the login process, the server ensures that only authorized operations are performed by the user.
Specifically, each servlet that extracts information or manipulates the database verifies that the operation does not violate the security area guaranteed to another user before executing the operation.
In conclusion, each operation is valid only if performed on database rows linked to the userId of the user stored in the session.



HTML VERSION

The connection between the LOGOUT element and the Logout servlet was represented only once in the IFML diagram to avoid overcomplicating the graphic.
In any case, the LOGOUT HTML element is always connected to the same servlet that performs the same action every time, even when this connection is not explicitly shown.

Each servlet performs checks on the session state and the correctness of the parameters. If the user is not in session (or the session does not exist), the server redirects to the Welcome servlet, which will display the PublicPage.html.
These connections are not explicitly shown, but they were made implicit to improve the readability of the diagram.
In case of parameter errors, the servlet will reload the page from which the request originated, adding an error message, provided that the error does not result in a security violation of the server or the database. Otherwise, an error page will be displayed.

In the sequence diagrams, the type of HTTP request is not specified; it was chosen to make it implicit.
The only servlets invoked with the doPost method are:

Login
Registration
CreateFolder
CreateSubfolder
CreateDocument
All others are invoked via the doGet method.



RIA VERSION

The entire JavaScript code that manages the client side once on the Home.html page has been implemented as a single IIFE, whose declaration and definition are found in the ShowHome.js document.
The remaining .js documents (excluding Public.js) serve as "libraries" of functions that will be called within the IIFE, in order to improve readability and future code maintenance.

It was deemed appropriate to include some sequence diagrams of the main operations. The messages originating from the HTML elements indicate the event that triggers the invocation of the JavaScript function.
The JavaScript element defines the page where the function is defined, while the launching message indicates the name of the invoked function.

In the sequence diagrams, the type of HTTP request is not specified; it was chosen to make it implicit.
The only servlets invoked with the doPost method are:

Login
Registration
CreateFolder
CreateSubfolder
CreateDocument
All others are invoked via the doGet method.
