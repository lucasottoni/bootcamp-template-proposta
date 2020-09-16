FROM openjdk:11

ENV APP_TARGET target
ENV APP card-proposal.jar

ARG JAR_FILE=${APP_TARGET}/${APP}
COPY ${JAR_FILE} ${APP}

CMD java -Dspring.profiles.active=${SPRING_PROFILES:-qa} ${JAVA_OPTS} -jar ${APP}
