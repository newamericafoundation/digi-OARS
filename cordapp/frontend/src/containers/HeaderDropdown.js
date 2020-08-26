import React from 'react';
import {
  CDropdown,
  CDropdownItem,
  CDropdownMenu,
  CDropdownToggle,
  CImg
} from '@coreui/react';
import CIcon from '@coreui/icons-react';
import { useAuth } from '../auth-hook'; 

const HeaderDropdown = () => {
  const auth = useAuth();

  function DropdownItems({isAuthenticated}) {
    if (isAuthenticated) {
      return(
        <React.Fragment>
          <CDropdownItem>
                <CIcon name="cil-user" className="mfe-2"/>{auth.user.fullName}
          </CDropdownItem>
          <CDropdownItem onClick={() => auth.logout()}>
                <CIcon name="cil-exit-to-app" className="mfe-2"/>Logout
          </CDropdownItem>
        </React.Fragment>
      )
    }
    return(
      <CDropdownItem onClick={() => auth.login()}>
        <CIcon name="cil-lock-locked" className="mfe-2"/>Login
      </CDropdownItem>
    )
  }

  return (
    <React.Fragment>
      <CDropdown
        inNav
        className="c-header-nav-items"
        direction="down"
      >
        <CDropdownToggle className="c-header-nav-link" caret={false}>
          {auth.user.email}
        </CDropdownToggle>
      </CDropdown>
      <CDropdown
        inNav
        className="c-header-nav-items"
        direction="down"
      >
        <CDropdownToggle className="c-header-nav-link" caret={true}>
          {auth.isAuthenticated &&
            <div>
              <div className="c-avatar">
                <CImg
                  src={'avatars/6.jpg'}
                  className="c-avatar-img"
                  alt={auth.user.email}
                />
              </div>
            </div>
          }
          {!auth.isAuthenticated && 
            <div className="c-avatar">
              <CIcon name="cil-user" />
            </div>
          }
        </CDropdownToggle>
        <CDropdownMenu className="pt-0" placement="bottom-end">
          <CDropdownItem
            header
            tag="div"
            color="light"
            className="text-center"
          >
            <strong>Account</strong>
          </CDropdownItem>
          <DropdownItems isAuthenticated={auth.isAuthenticated}/>
        </CDropdownMenu>
      </CDropdown>
    </React.Fragment>
  )
}

export default HeaderDropdown;
