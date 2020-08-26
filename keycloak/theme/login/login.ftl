<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=social.displayInfo displayWide=(realm.password && social.providers??); section>
<#if section = "header">
<#elseif section = "form">
    <div class="container">
        <div class="justify-content-center row">
            <div class="col-md-8">
                <div class="card-group">
                    <div class="p-4 card">
                        <div class="card-body">
                            <form class="" id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post">
                                <div class="row">
                                    <div class="col-md-6">
                                        <h1>${msg("doLogIn")}</h1>
                                    </div>
                                    <div class="col-md-6 input-group">
                                        <#if realm.internationalizationEnabled  && locale.supported?size gt 1>
                                        <div id="kc-locale">
                                            <div id="kc-locale-wrapper" class="${properties.kcLocaleWrapperClass!}">
                                                <div class="dropdown">
                                                    <a class="btn btn-secondary dropdown-toggle" href="#" role="button" id="dropdownMenuLink" data-toggle="dropdown" aria-expanded="false">${locale.current}</a>
                                                    <div class="dropdown-menu" aria-labelledby="dropdownMenuLink">
                                                        <a class="dropdown-item" href="#" id="kc-current-locale-link">${locale.current}</a>
                                                            <#list locale.supported as l>
                                                                <a class="dropdown-item" href="${l.url}">${l.label}</a>
                                                            </#list>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        </#if>
                                    </div>
                                </div>
                                <p class="text-muted">${msg("loginTitle", (realm.displayName!''))}</p>
                                <div class="input-group mb-3">
                                    <div class="input-group-prepend">
                                        <div class="input-group-text cil-user"></div>
                                    </div>
                                    <#if usernameEditDisabled??>
                                        <input tabindex="1" id="username" class="${properties.kcInputClass!}" name="username" value="${(login.username!'')}" type="text" disabled />
                                    <#else>
                                        <input tabindex="1" id="username" class="${properties.kcInputClass!}" name="username" value="${(login.username!'')}"  type="text" autofocus placeholder="${msg('usernameOrEmail')}" autocomplete="off" />
                                    </#if>
                                </div>
                                <div class="input-group mb-4">
                                    <div class="input-group-prepend">
                                        <div class="input-group-text cil-lock-locked"></div>
                                    </div>
                                    <input class="${properties.kcInputClass!}" tabindex="2" name="password" type="password" placeholder="${msg('password')}" autocomplete="off">
                                </div>
                                <#if message?has_content && (message.type != 'warning' || !isAppInitiatedAction??)>
                                    <div class="alert alert-${message.type}">
                                        <#if message.type = 'success'><span class="${properties.kcFeedbackSuccessIcon!} text-success"></span></#if>
                                        <#if message.type = 'warning'><span class="${properties.kcFeedbackWarningIcon!}"></span></#if>
                                        <#if message.type = 'error'><span class="${properties.kcFeedbackErrorIcon!} text-danger"></span></#if>
                                        <#if message.type = 'info'><span class="${properties.kcFeedbackInfoIcon!}"></span></#if>
                                        <span class="kc-feedback-text">${kcSanitize(message.summary)?no_esc}</span>
                                    </div>
                                </#if>
                                <#if realm.rememberMe && !usernameEditDisabled??>
                                <div class="input-group mb-0">
                                <div class="checkbox">
                                    <label>
                                    <#if login.rememberMe??>
                                    <input tabindex="3" id="rememberMe" name="rememberMe" type="checkbox" checked> ${msg("rememberMe")}
                                    <#else>
                                    <input tabindex="3" id="rememberMe" name="rememberMe" type="checkbox"> ${msg("rememberMe")}
                                    </#if>
                                    </label>
                                </div></div>
                                </#if>
                                <div id="kc-form-buttons" class="${properties.kcFormGroupClass!}">
                                    <input type="hidden" id="id-hidden-input" name="credentialId" <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>/>
                                    <input tabindex="4" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" name="login" id="kc-login" type="submit" value="${msg("doLogIn")}"/>
                                </div>
                                <div class="input-group mb-0">
                                    <#if realm.resetPasswordAllowed>
                                    <a href="${url.loginResetCredentialsUrl}">${msg("doForgotPassword")}</a>
                                    </#if>
                                </div>
                            </form>
                        </div>
                    </div>
                    <div class="text-white bg-primary py-5 d-md-down-none card" style="width: 44%;">
                        <div class="text-center card-body">
                            <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
                            <div id="kc-registration">
                                <h2>${msg("doRegister")}</h2>
                                <p>Become a member of the application by signing up here!</p>
                                <a href="${url.registrationUrl}"><button class="mt-3 btn btn-secondary active" type="button" tabindex="-1">${msg("doRegister")}</button></a>
                            </div>
                            </#if>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
<#elseif section = "info" >
</#if>
</@layout.registrationLayout>