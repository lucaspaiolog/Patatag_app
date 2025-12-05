<img width="856" height="291" alt="logopatatag" src="https://github.com/user-attachments/assets/750f6b55-1154-4d0a-be94-1c05c1b12d07" />

# 🐾 PATATAG App - Monitoramento Mobile

Aplicativo Android nativo para o sistema **Patatag**, permitindo o rastreamento GPS de pets em tempo real, visualização de histórico e gerenciamento de cercas virtuais diretamente pelo smartphone.

> Este projeto faz parte da disciplina de **Laboratório de Desenvolvimento Web** no curso de **Desenvolvimento de Software Multiplataforma** na **Fatec Praia Grande**.

---

## 📱 Sobre o App

O **Patatag App** atua como o cliente mobile para o ecossistema Patatag. Enquanto o dispositivo ESP32 envia a localização e o servidor Web (Flask) processa os dados, o aplicativo Android oferece uma interface amigável para o tutor monitorar seu pet de qualquer lugar.

### Principais Funcionalidades

- ✅ **Login e Cadastro:** Autenticação segura integrada ao backend via API REST.
- ✅ **Lista de Pets:** Visualização rápida do status (Online/Offline) e nível de bateria de todos os pets cadastrados.
- ✅ **Rastreamento em Tempo Real:** Integração com Google Maps para mostrar a localização exata do pet selecionado.
- ✅ **Gerenciamento de Pets:** Adicionar novos pets com foto (integração com câmera/galeria) e upload automático para o servidor.
- ✅ **Alertas:** Visualização de notificações importantes, como bateria fraca ou saída de cerca virtual.
- ✅ **Perfil:** Gerenciamento de dados do usuário e logout.

---

## 🛠 Tecnologias Utilizadas

- **Linguagem:** Kotlin
- **Arquitetura:** MVC / ViewBinding
- **Comunicação de Rede:** Retrofit2 + Gson + OkHttp3
- **Mapas:** Osmdroid
- **Carregamento de Imagens:** Glide
- **Design:** Material Design Components (XML)

---

## 🚀 Como Rodar o Projeto

### Pré-requisitos

1. **Backend Rodando:** O servidor [Patatag Web](https://github.com/seu-usuario/patatag-web) deve estar rodando na mesma rede (Wi-Fi) ou em um IP público acessível pelo celular/emulador.
2. **Android Studio:** Versão Ladybug ou superior recomendada.
3. **Chave de API do Google Maps:** Uma chave válida com a "Maps SDK for Android" ativada no Google Cloud Console.

### Passo a Passo

1. **Clonar o repositório**
   ```bash
   git clone [https://github.com/lucaspaiolog/patatag_app.git](https://github.com/lucaspaiolog/patatag_app.git)
Configurar o Endereço do Servidor O app precisa saber onde está o seu backend Python/Flask para fazer as requisições.

Abra o arquivo: app/src/main/java/br/edu/fatecpg/patatagapp/api/RetrofitClient.kt

Localize a constante BASE_URL.

Altere para o IP da sua máquina onde o servidor Flask está rodando.
```bash
// Exemplo: Se seu PC tem o IP 192.168.15.10
private const val BASE_URL = "[http://192.168.15.10:5000/](http://192.168.15.10:5000/)"

// IMPORTANTE:
// Se estiver usando o Emulador do Android Studio na mesma máquina do servidor:
// private const val BASE_URL = "[http://10.0.2.2:5000/](http://10.0.2.2:5000/)"
```

Conecte seu dispositivo Android via USB (com Depuração USB ativa) ou inicie um emulador no Android Studio.

Clique no botão "Run" (▶️).

📂 Estrutura do Projeto
  ```bash
  PatatagApp/
  ├── app/
  │   ├── src/
  │   │   ├── main/
  │   │   │   ├── java/br/edu/fatecpg/patatagapp/
  │   │   │   │   ├── adapter/       # Adaptadores para RecyclerView (Listas de Pets e Alertas)
  │   │   │   │   ├── api/           # Interfaces Retrofit e Modelos de Dados (DTOs)
  │   │   │   │   ├── model/         # Modelos de domínio local
  │   │   │   │   ├── utils/         # Utilitários (Gerenciador de Sessão)
  │   │   │   │   ├── view/          # Activities (Telas: Login, Home, Mapa, etc.)
  │   │   │   │   └── MainActivity   # Ponto de entrada
  │   │   │   ├── res/
  │   │   │   │   ├── layout/        # Arquivos XML de layout das telas
  │   │   │   │   ├── drawable/      # Ícones, vetores e backgrounds
  │   │   │   │   ├── mipmap/        # Ícones do aplicativo
  │   │   │   │   └── values/        # Cores, strings, temas e estilos
  │   │   │   └── AndroidManifest.xml # Configurações e Permissões
  │   └── build.gradle.kts           # Dependências do módulo app
  └── build.gradle.kts
  ```
---

## 👥 Autores

- **Lucas Paiolo**
- **Kevin Flay**
- **Gael Mormile**
- **Marcos Antonio**

---

## 🙏 Agradecimentos

Gostaríamos de expressar nossa profunda gratidão a todos que tornaram este projeto possível:

À **Fatec Praia Grande**, pela infraestrutura e pela excelência no ensino proporcionado no curso de Desenvolvimento de Software Multiplataforma.

À nossa orientadora, **Prof.ª Eulaliane Aparecida Gonçalves**, por todo o suporte, paciência e conhecimento compartilhado, fundamentais para a concretização deste trabalho.

---
