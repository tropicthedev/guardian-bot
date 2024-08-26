import { createFileRoute } from '@tanstack/react-router'
import Header from '../../components/header'
import QuestionForm from '../../components/forms/question'

export const Route = createFileRoute('/applications/create')({
  component: ApplicationsCreatePage
})

function ApplicationsCreatePage() {
  return (
    <>
      <Header />
      <QuestionForm />
    </>
  )
}