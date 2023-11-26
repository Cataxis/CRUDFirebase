// Definición de la clase Libro como modelo de datos
data class Libro(
    var id: String = "",        // Identificador único del libro
    var Nombre: String = "",    // Nombre del libro
    var Autor: String = "",     // Nombre del autor del libro
    var Editorial: String = ""  // Nombre de la editorial del libro
) {
    // Override de la función toString para proporcionar una representación legible en texto
    override fun toString(): String {
        return "$Nombre - $Autor - $Editorial"
    }
}
