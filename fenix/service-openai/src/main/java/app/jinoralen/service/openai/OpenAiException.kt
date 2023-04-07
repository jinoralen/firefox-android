package app.jinoralen.service.openai

class OpenAiException(message: String, val throwable: Throwable): Exception(message)

class OpenAiNoChoiceException: Exception("No summary available")
