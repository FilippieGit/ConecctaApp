import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EmailSender {

    // Estrutura de dados para representar uma candidatura
    data class Candidatura(
        val id_candidatura: Int,
        val vaga_id: Int,
        val user_id: Int,
        val respostas: String, // JSON string
        val data_candidatura: String,
        val status: String,
        val data_atualizacao: String,
        val motivo_rejeicao: String?,
        val recrutador_id: Int
    )

    // Função para enviar emails para aprovados
    fun enviarEmailsAprovados(activity: AppCompatActivity, candidaturas: List<Candidatura>) {
        // Filtrar apenas candidaturas aprovadas
        val aprovados = candidaturas.filter { it.status.equals("aprovada", ignoreCase = true) }

        if (aprovados.isEmpty()) {
            // Não há aprovados para enviar email
            return
        }

        // Para cada candidato aprovado, enviar um email
        for (candidatura in aprovados) {
            enviarEmailIndividual(
                activity,
                emailDestinatario = "candidato${candidatura.user_id}@exemplo.com", // Substitua pela forma de obter o email real
                nomeDestinatario = "Candidato ${candidatura.user_id}", // Substitua pelo nome real
                vagaId = candidatura.vaga_id
            )
        }
    }

    // Função para enviar um email individual
    private fun enviarEmailIndividual(
        activity: AppCompatActivity,
        emailDestinatario: String,
        nomeDestinatario: String,
        vagaId: Int
    ) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$emailDestinatario")  // Importante colocar o email no URI
                putExtra(Intent.EXTRA_SUBJECT, "Parabéns! Você foi aprovado para a vaga $vagaId")
                putExtra(
                    Intent.EXTRA_TEXT,
                    """
                Prezado(a) $nomeDestinatario,

                É com grande satisfação que informamos que você foi aprovado(a) no processo seletivo para a vaga $vagaId!

                Em breve nosso time de RH entrará em contato com os próximos passos.

                Atenciosamente,
                Equipe de Recrutamento
                """.trimIndent()
                )
            }

            if (intent.resolveActivity(activity.packageManager) != null) {
                activity.startActivity(Intent.createChooser(intent, "Enviar email usando:"))
            } else {
                Toast.makeText(activity, "Nenhum app de email instalado.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(activity, "Erro ao tentar enviar email.", Toast.LENGTH_LONG).show()
        }
    }

}