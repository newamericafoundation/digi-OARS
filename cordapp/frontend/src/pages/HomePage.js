import React, { useState, useEffect } from 'react';
import {
  CButton,
  CCard,
  CCardBody,
  CCardFooter,
  CCol,
  CProgress,
  CRow,
  CJumbotron
} from '@coreui/react'
import CIcon from '@coreui/icons-react'
import { useAuth } from '../auth-hook';

const HomePage = () => {
  const auth = useAuth();
  const [hour, setHour] = useState();
  const [greeting, setGreeting] = useState();

  useEffect(() => {
    const fetchGreeting = async() => {
      const date = new Date();    
      setHour(date.getHours());

      if (hour) {
        switch(true) {
          case (hour < 12):
            setGreeting("Good morning, ");
            break;
          case (hour > 12 && hour < 17):
            setGreeting("Good afternoon, ");
            break;
          case (hour > 17):
            setGreeting("Good evening, ");
            break;
          default:
            setGreeting("Hello, ");
            break;
        }
      }
    }
    if (auth.isAuthenticated) {
      fetchGreeting();
    }
  }, [hour, greeting, auth]);

  return (
    <>
      <CJumbotron className="bg-dark">
        <CRow>
          <CCol>
            {!auth.isAuthenticated ? 
              <h1 className="display-3">Welcome to OARS!</h1> :
              <h1 className="display-3">{greeting}{auth.user.firstName}!</h1>
            }
            <p className="lead">The Open Asset Repatriation System increases transparency and accountability in the asset return process.</p>
            <p>For more information visit website</p>
            {!auth.isAuthenticated && <CButton color="primary" onClick={() => auth.login()}>Login</CButton>}
            {auth.isAuthenticated && <CButton color="light" onClick={() => auth.logout()}>Logout</CButton>}
          </CCol>
        </CRow>
      </CJumbotron>
      {auth.isAuthenticated &&
      <CCard>
        <CCardBody>
          <CRow>
            <CCol sm="5">
              <h1 id="traffic" className="card-title mb-0">Home</h1>
            </CCol>
            <CCol sm="7" className="d-none d-md-block">
              <CButton color="primary" className="float-right">
                <CIcon name="cil-cloud-download"/>
              </CButton>
            </CCol>
          </CRow>
        </CCardBody>
        <CCardFooter>
          <CRow className="text-center">
            <CCol md sm="12" className="mb-sm-2 mb-0">
              <div className="text-muted">Total Assets Repatriated</div>
              <strong>$5,000,000</strong>
              <CProgress
                className="progress-xs mt-2"
                precision={1}
                color="success"
                value={40}
              />
            </CCol>
            <CCol md sm="12" className="mb-sm-2 mb-0 d-md-down-none">
              <div className="text-muted">Blah</div>
              <strong>24.093</strong>
              <CProgress
                className="progress-xs mt-2"
                precision={1}
                color="info"
                value={40}
              />
            </CCol>
            <CCol md sm="12" className="mb-sm-2 mb-0">
              <div className="text-muted">Blah</div>
              <strong>78.706</strong>
              <CProgress
                className="progress-xs mt-2"
                precision={1}
                color="warning"
                value={40}
              />
            </CCol>
          </CRow>
        </CCardFooter>
      </CCard>}
  </>
  )
}

export default HomePage;
