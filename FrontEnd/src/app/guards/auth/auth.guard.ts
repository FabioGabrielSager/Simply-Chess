import {CanActivateFn, Router} from '@angular/router';
import {SessionService} from "../../services/session.service";
import {inject} from "@angular/core";
import {ToastService} from "../../services/toast.service";

export const authGuard: CanActivateFn = (route, state) => {
  const sessionService: SessionService = inject(SessionService);
  const router: Router = inject(Router);
  const toastService: ToastService = inject(ToastService);

  if(sessionService.isLogged()) {
    return true;
  }

  toastService.show("Debe ingresar un usuario primero", "bg-danger");
  return router.parseUrl('/home');
};
