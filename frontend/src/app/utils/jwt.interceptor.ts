import {Injectable} from "@angular/core";
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {UserService} from "../shared/services/user.service";

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private userService: UserService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if(request.headers.get('skip') === 'true'){
      const newHeaders = request.headers.delete("skip");
      const newRequest= request.clone({headers: newHeaders});
      return next.handle(newRequest);
    }
    const isApiUrl = request.url.startsWith(environment.apiUrl);
    const isLoggedIn = this.userService.isLoggedIn;
    const at = this.userService.getAccessToken();
    let isAccessTokenValid = at && this.userService.isTokenValid(at);
    if(isLoggedIn && !isAccessTokenValid) {
      this.userService.refreshTokens()
        .subscribe({
          next: () => {
            isAccessTokenValid = true
          },
          error: err => {
            console.log(err);
            this.userService.logout();
          }
        });

    }
    if (isLoggedIn && isApiUrl && isAccessTokenValid) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${at}`
        }
      });
    }
    return next.handle(request);
  }
}
