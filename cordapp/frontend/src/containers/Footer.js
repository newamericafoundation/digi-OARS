import React from 'react'
import { CFooter } from '@coreui/react'

const Footer = () => {
  return (
    <CFooter fixed={false}>
      <div>
        <span className="ml-1">&copy; 2020 New America & R3 Ltd.</span>
      </div>
      <div className="mfs-auto">
        <span className="mr-1">Powered by</span>
        <a href="https://corda.net" target="_blank" rel="noopener noreferrer">Corda</a>
      </div>
    </CFooter>
  )
}

export default React.memo(Footer)
