import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import CIcon from '@coreui/icons-react';
import {
  CCreateElement,
  CSidebar,
  CSidebarBrand,
  CSidebarNav,
  CSidebarNavDivider,
  CSidebarNavTitle,
  CSidebarMinimizer,
  CSidebarNavDropdown,
  CSidebarNavItem,
} from '@coreui/react';
import { useAuth } from '../auth-hook';

import nav from './_nav';
import nav_secure from './_nav_secure';

const Sidebar = () => {
    const dispatch = useDispatch()
    const show = useSelector(state => state.sidebarShow)
    const auth = useAuth();

    return (
        <CSidebar show={show} onShowChange={(val) => dispatch({type: 'set', sidebarShow: val})}>
            <CSidebarBrand className='d-md-down-none' to="/">
                <CIcon
                    className="c-sidebar-brand-full"
                    name="logo-negative"
                    height={35}
                />
                <CIcon
                    className="c-sidebar-brand-minimized"
                    name="sygnet"
                    height={35}
                />
            </CSidebarBrand>
            <CSidebarNav>
                {/* Publicly visible items */}
                <CCreateElement
                    items={nav}
                    components={{
                        CSidebarNavDivider,
                        CSidebarNavDropdown,
                        CSidebarNavItem,
                        CSidebarNavTitle
                    }}
                />
                {/* Privately visible items to role: RealmAdmin | RealmUser */}
                {auth.meta.keycloak.hasResourceRole('user') && <CCreateElement
                    items={nav_secure}
                    components={{
                        CSidebarNavDivider,
                        CSidebarNavDropdown,
                        CSidebarNavItem,
                        CSidebarNavTitle
                    }}
                />}
            </CSidebarNav>
            <CSidebarMinimizer className="c-d-md-down-none"/>
        </CSidebar>
    )
}

export default Sidebar;