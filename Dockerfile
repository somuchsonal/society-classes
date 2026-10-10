FROM tomcat:9.0.74-jre17-temurin-focal
#RUN mkdir /usr/local/tomcat
#CMD ["catalina.sh", "run","-D", "FOREGROUND"]
ENV LANG='utf8mb4_general_ci' LANGUAGE='utf8mb4'	
RUN mkdir /app
RUN apt-get update
RUN apt-get install -y python3-pip
RUN pip install boto3
RUN pip install google-cloud-secret-manager
COPY /build/application.jar /app/application.jar
COPY /build/Procfile-prod /app
COPY secrets.py /app
COPY start.sh /app
RUN chmod 777 /app/secrets.py
RUN chmod 777 /app/start.sh
WORKDIR /app
CMD "/app/start.sh"
EXPOSE 8080
