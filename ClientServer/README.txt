How to run 

GET /
httpc get / http://G:\workspace\CNProgramming2

GET /foo.txt 
httpc get /HelloWorld.txt http://G:\workspace\CNProgramming2
httpc get /FileNotExist.txt http://G:\workspace\CNProgramming2
httpc get /BasicHTML.html http://G:\workspace\CNProgramming2 
httpc get /note.xml http://G:\workspace\CNProgramming2
httpc get /HelloWorld.html http://G:\workspace\CNProgramming2
httpc get /simple.xml http://G:\workspace\CNProgramming2

POST /bar.txt
httpc post /HelloWorld.txt -d '{"Assignment": 1}' http://G:\workspace\CNProgramming2
httpc post /HelloWorld.txt -f FilesList.txt http://G:\workspace\CNProgramming2
httpc post /HelloWorld.txt -f FilesList.txt http://G:\workspace\CNProgramming2 
httpc post /HelloWorld.txt -d '{"Assignment":2}' http://G:\workspace\CNProgramming2 

Security Handling
httpc get /HelloWorld.txt -d '{"Assignment":2}' http://G:\workspace 

Error Handling
httpc get /HelloWorld.txt http://G:\workspace\CNProgramming2
httpc get /FileNotExist.txt http://G:\workspace\CNProgramming2
httpc get / http://OutOfDirectory

Content-Disposition
httpc get / http://G:\workspace\CNProgramming2 -Content-Disposition:attachment FilesList.txt
httpc post /HelloWorld.txt -d '{"Assignment": 1}' http://G:\workspace\CNProgramming2 -Content-Disposition:attachment tmp.txt 
  