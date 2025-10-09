import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import DreamDirector from '../views/DreamDirector.vue'
import Manus from '../views/Manus.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/dream-director',
    name: 'DreamDirector',
    component: DreamDirector
  },
  {
    path: '/manus',
    name: 'Manus',
    component: Manus
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

