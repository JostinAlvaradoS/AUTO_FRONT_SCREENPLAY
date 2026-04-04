# language: es
@HU-Waitlist @ListaEspera
Característica: Sistema de Lista de Espera Inteligente — UI Screenplay
  Como usuario interesado en un evento agotado
  Quiero unirme a la lista de espera desde la UI
  Para recibir un asiento cuando uno esté disponible

  @RegistroExitoso
  Escenario: Registro exitoso en lista de espera
    Dado que el evento "Concierto Rock 2026" está agotado y el usuario navega a su página
    Cuando el usuario se une a la waitlist con el email "jostin@example.com"
    Entonces la UI muestra el mensaje de éxito de waitlist
    Y el usuario recibe su posición en la cola de espera

  @TicketsDisponibles
  Escenario: Intento de registro con tickets disponibles
    Dado que el evento "Concierto Rock 2026" tiene tickets disponibles
    Cuando el usuario navega a la página del evento con stock
    Entonces el botón de waitlist no es visible en la UI

  @RegistroDuplicado
  Escenario: Registro duplicado en la misma lista
    Dado que "jostin@example.com" ya está registrado en la lista del evento vía UI
    Cuando el mismo correo intenta unirse al waitlist nuevamente
    Entonces la UI muestra el mensaje de conflicto de la API

  @AsignacionAutomatica
  Escenario: Asignación automática al expirar una reserva
    Dado que "jostin@example.com" es el primero en la lista de espera del evento
    Cuando el tiempo de reserva caduca
    Entonces la API confirma que ya no hay pendientes en la cola
    Y actualiza el estado de la entrada a Asignado
    Y envía un correo con el enlace de pago con validez de 30 minutos

  @LiberacionConSiguiente
  Escenario: Liberación por inacción con siguiente en cola
    Dado que "jostin@example.com" fue asignado y no realizó el pago en 30 minutos
    Y "segundo@example.com" está en la lista de espera del evento
    Cuando el sistema detecta que el primer usuario no pagó
    Entonces la API confirma la rotación del asiento al siguiente usuario
    Y el asiento no fue liberado al pool general
    Y envía correo de pago al siguiente con validez de 30 minutos

  @LiberacionSinCola
  Escenario: Liberación por inacción con cola vacía
    Dado que "jostin@example.com" fue asignado y no realizó el pago en 30 minutos
    Y no hay más usuarios esperando en la lista del evento
    Cuando el sistema detecta que el usuario no pagó
    Entonces la API confirma que el asiento fue liberado al pool general
