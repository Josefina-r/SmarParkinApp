import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    val id: Int,
    val username: String,
    val email: String,
    val first_name: String?,
    val last_name: String?,
    val telefono: String?,
    val rol: String?,
    val rol_display: String?,
    val is_admin: Boolean?,
    val is_owner: Boolean?,
    val is_client: Boolean?,
    val tipo_documento: String?,
    val numero_documento: String?,
    val fecha_nacimiento: String?,
    val direccion: String?,
    val codigo_postal: String?,
    val pais: String?
)
data class UpdateProfileRequest(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName("email") val email: String? = null,
    @SerializedName("tipo_documento") val tipoDocumento: String?,
    @SerializedName("numero_documento") val numeroDocumento: String?,
    @SerializedName("fecha_nacimiento") val fechaNacimiento: String?,
    @SerializedName("direccion") val direccion: String?,
    @SerializedName("codigo_postal") val codigoPostal: String?,
    @SerializedName("pais") val pais: String?
)