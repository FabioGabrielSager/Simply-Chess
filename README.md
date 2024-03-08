# Simply Chess
Plataforma multijugador que permite a los usuarios jugar partidas de ajedrez en tiempo real a través de web sockets. Una de las características principales de la plataforma es la capacidad de crear y unirse a partidas privadas, lo que permite a los jugadores competir entre sí de manera segura y privada.

Además, el proyecto incluye una implementación de cola para buscar partidas, que agiliza el proceso de encontrar oponentes y garantiza una experiencia de juego fluida y sin interrupciones. Esta función mejora significativamente la accesibilidad y la diversión del juego, al tiempo que ofrece a los jugadores una forma conveniente de encontrar desafíos en cualquier momento.

## Tecnologías utilizadas.
- Spring boot.
- Websocket.
- AngularJS.
- Bootstrap.
- StompJS.
- SockJS.
- RxJS.

## Cómo correr la aplicación?
1. Dirigirse al directorio BackEnd/.compose `cd BackEnd/.compose`.
2. Levantar compose `sudo docker compose up`.
   - *Asegúrese de tener los puertos 8080, 8081 y 5432 disponibles.*
3. Dirigirse al directorio FrondEnd/ `cd FronEnd`.
4. Correr aplicación en modo developer `ng serve` o `npm start`.
   - *Debidos a la configuraciones de CORS el FronEnd/ debe ser levantada de manera local en el puerto por defecto de la aplicaciones de AngularJS, es decir, el puerto 4200.*
   - *La aplicación de interfaz gráfica aún no ha sido configurada y optimizada para ser desplegada en producción.*
