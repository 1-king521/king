import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import { NavLabel } from "@/enums";
import { useConfigureStore } from "@/store/configure";
const routes: Array<RouteRecordRaw> = [
  {
    path: "/:pathMatch(.*)*",
    redirect: "/404",
  },
  {
    path: "/404",
    component: () => import("@/pages/error/404.vue"),
  },
  {
    path: "/",
    name: "wyk-app-layout",
    component: () => import("@/components/layouts/WykAppLayout.vue"),
    children: [
      {
        path: "/",
        name: "home",
        component: () => import("@/pages/home/Home.vue"),
      },
      {
        path: "/sign-in",
        name: "sign-in",
        component: () => import("@/pages/auth/SignIn.vue"),
      },
      {
        path: "/sign-up",
        name: "sign-up",
        component: () => import("@/pages/auth/SignUp.vue"),
      },
      {
        path: "/forgot-password",
        name: "forgot-password",
        component: () => import("@/pages/auth/ForgotPassword.vue"),
      },
      {
        path: "/personal",
        name: "personal",
        meta: {
          requireAuth: true,
        },
        component: () => import("@/pages/user/Personal.vue"),
      },
      {
        path: "/song-sheet",
        name: "song-sheet",
        component: () => import("@/pages/song-sheet/SongSheet.vue"),
      },
      {
        path: "/song-sheet-detail/:id",
        name: "song-sheet-detail",
        component: () => import("@/pages/song-sheet/SongSheetDetail.vue"),
      },
      {
        path: "/singer",
        name: "singer",
        component: () => import("@/pages/singer/Singer.vue"),
      },
      {
        path: "/song-rank",
        name: "song-rank",
        component: () => import("@/pages/rank/SongRank.vue"),
      },
      {
        path: "/playlist-rank",
        name: "playlist-rank",
        component: () => import("@/pages/rank/PlaylistRank.vue"),
      },
      {
        path: "/singer-detail/:id",
        name: "singer-detail",
        component: () => import("@/pages/singer/SingerDetail.vue"),
      },
      {
        path: "/lyric/:id",
        name: "lyric",
        component: () => import("@/pages/lyric/Lyric.vue"),
      },
      {
        path: "/search",
        name: "search",
        component: () => import("@/pages/search/Search.vue"),
      },
      {
        path: "/personal-data",
        name: "personal-data",
        component: () => import("@/pages/user/PersonalData.vue"),
      },
      {
        path: "/setting",
        name: "setting",
        meta: {
          requireAuth: true,
        },
        component: () => import("@/pages/user/Setting.vue"),
        children: [
          {
            path: "/setting/PersonalData",
            name: "personalData",
            meta: {
              requireAuth: true,
            },
            component: () => import("@/pages/user/PersonalData.vue"),
          }
        ]
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

/** 与顶栏主导航一致的路径 → 菜单高亮文案（含直达 /song-rank 等） */
const MAIN_NAV_PATH_TO_LABEL: Record<string, string> = {
  "/": NavLabel.Home,
  "/song-sheet": NavLabel.SongSheet,
  "/singer": NavLabel.Singer,
  "/song-rank": NavLabel.SongRank,
  "/playlist-rank": NavLabel.PlaylistRank,
};

router.afterEach((to) => {
  const label = MAIN_NAV_PATH_TO_LABEL[to.path];
  if (label) {
    useConfigureStore().setActiveNavName(label);
  }
});

router.beforeEach((to) => {
  const needAuth = to.matched.some((record) => record.meta.requireAuth);
  if (!needAuth) {
    return;
  }
  const token = useConfigureStore().token;
  if (token) {
    return;
  }
  return { path: "/sign-in", query: { redirect: to.fullPath } };
});

export default router;
