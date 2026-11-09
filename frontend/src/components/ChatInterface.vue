<template>
  <div class="chat-container">
    <div class="chat-messages" ref="messagesRef">
      <div v-for="(msg, idx) in messages" :key="idx" class="message" :class="msg.type">
        <div class="message-content">{{ msg.content }}</div>
        <div class="message-time">{{ msg.time }}</div>
      </div>
    </div>
    <div class="chat-input-area">
      <input
        v-model="inputMessage"
        @keyup.enter="sendMessage"
        placeholder="Ask about your predictions, routines, or intent..."
        class="chat-input"
      />
      <button @click="sendMessage" class="send-btn" :disabled="!inputMessage.trim()">
        Send
      </button>
    </div>
    <div class="quick-questions">
      <button v-for="q in quickQuestions" :key="q" @click="askQuestion(q)">
        {{ q }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { chatService } from '../services/api'

const props = defineProps({
  userId: String
})

const messages = ref([])
const inputMessage = ref('')
const messagesRef = ref(null)

const quickQuestions = [
  'Where am I going?',
  'Why this prediction?',
  'What is my routine?',
  'What is my intent?'
]

const sendMessage = async () => {
  if (!inputMessage.value.trim()) return

  const userMsg = {
    type: 'user',
    content: inputMessage.value,
    time: new Date().toLocaleTimeString()
  }
  messages.value.push(userMsg)

  try {
    const response = await chatService.sendMessage({
      userId: props.userId,
      message: inputMessage.value
    })

    messages.value.push({
      type: 'bot',
      content: response.data.response,
      time: new Date().toLocaleTimeString()
    })
  } catch (err) {
    messages.value.push({
      type: 'bot',
      content: 'Sorry, I could not process your request. Please try again.',
      time: new Date().toLocaleTimeString()
    })
  }

  inputMessage.value = ''
  scrollToBottom()
}

const askQuestion = (question) => {
  inputMessage.value = question
  sendMessage()
}

const scrollToBottom = () => {
  setTimeout(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  }, 100)
}

onMounted(() => {
  messages.value.push({
    type: 'bot',
    content: 'Hello! I am your Cognitive GPS assistant. Ask me about your predictions, movement patterns, or intent.',
    time: new Date().toLocaleTimeString()
  })
})
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 300px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 1rem 0;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.message {
  max-width: 85%;
  padding: 0.75rem 1rem;
  border-radius: 12px;
}

.message.user {
  align-self: flex-end;
  background: #38bdf8;
  color: #0f172a;
}

.message.bot {
  align-self: flex-start;
  background: #334155;
  color: #e2e8f0;
}

.message-time {
  font-size: 0.625rem;
  opacity: 0.7;
  margin-top: 0.25rem;
}

.chat-input-area {
  display: flex;
  gap: 0.5rem;
  margin-top: 1rem;
}

.chat-input {
  flex: 1;
  background: #0f172a;
  border: 1px solid #334155;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  color: #e2e8f0;
  font-size: 0.875rem;
}

.chat-input:focus {
  outline: none;
  border-color: #38bdf8;
}

.send-btn {
  background: #38bdf8;
  color: #0f172a;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quick-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-top: 0.75rem;
}

.quick-questions button {
  background: transparent;
  border: 1px solid #334155;
  color: #94a3b8;
  padding: 0.375rem 0.75rem;
  border-radius: 16px;
  font-size: 0.75rem;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-questions button:hover {
  border-color: #38bdf8;
  color: #38bdf8;
}
</style>
