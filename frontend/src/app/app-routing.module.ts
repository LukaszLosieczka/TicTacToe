import {RouterModule, Routes} from '@angular/router';
import {LoginLayoutComponent} from "./layouts/login-layout/login-layout.component";
import {LoginComponent} from "./user/components/login/login.component";
import {RegisterComponent} from "./user/components/register/register.component";
import {AuthGuard} from "./utils/auth.guard";
import {NgModule} from "@angular/core";
import {HomeLayoutComponent} from "./layouts/home-layout/home-layout.component";
import {GameComponent} from "./game/components/game/game.component";
import {LeaderBoardComponent} from "./game/components/leader-board/leader-board.component";

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/leader-board'
  },
  {
    path: '',
    component: HomeLayoutComponent,
    children: [
      {
        path: 'game',
        component: GameComponent
      },
      {
        path: 'leader-board',
        component: LeaderBoardComponent
      }
      ]
  },
  {
    path: '',
    component: LoginLayoutComponent,
    children: [
      {
        path: 'login',
        canActivate: [AuthGuard],
        component: LoginComponent
      },
      {
        path: 'register',
        canActivate: [AuthGuard],
        component: RegisterComponent
      }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
