import React from 'react'
import {
  CCol,
  CRow,
  CContainer,
} from '@coreui/react'

const Page503 = () => {
  return (
    <div className="c-app c-default-layout flex-row align-items-center">
      <CContainer>
        <CRow className="justify-content-center">
          <CCol md="6">
            <div className="clearfix">
              <h1 className="float-left display-3 mr-4">503</h1>
              <h4 className="pt-3">Something went wrong...</h4>
              <p className="text-muted float-left">The server is unavailable to handle this request right now.</p>
            </div>
          </CCol>
        </CRow>
      </CContainer>
    </div>
  )
}

export default Page503;
