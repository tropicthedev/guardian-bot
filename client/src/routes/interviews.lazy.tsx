import { createLazyFileRoute } from '@tanstack/react-router'
import Header from '../components/header'

export const Route = createLazyFileRoute('/interviews')({
  component: InterviewsPage
})

function InterviewsPage() {
  return (
    <Header />
  )
}