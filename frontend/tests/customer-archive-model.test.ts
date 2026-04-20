import assert from 'node:assert/strict'
import test from 'node:test'
import { appendContractRecord, appendDebtRecord, appendRiskRecord, createEmptyCustomerArchiveForm } from '../src/views/customer/customerArchiveState.ts'

test('append helpers keep archive subsections editable', () => {
  const form = createEmptyCustomerArchiveForm()

  appendRiskRecord(form)
  appendDebtRecord(form)
  appendContractRecord(form)

  assert.equal(form.riskRecords.length, 2)
  assert.equal(form.debtRecords.length, 2)
  assert.equal(form.contractRecords.length, 2)
})
