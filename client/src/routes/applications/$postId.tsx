import { createFileRoute } from '@tanstack/react-router'
import AnswerForm from '../../components/forms/answer'

export const Route = createFileRoute('/applications/$postId')({
  component: ApplicationPage
})

function ApplicationPage() {
  return (
    <>
      <AnswerForm />
    </>
  )
}