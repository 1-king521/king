import { defineStore } from "pinia";

interface ConfigureState {
  /** JWT；空字符串表示未登录 */
  token: string;
  showAside: boolean;
  showSongCommentDialog: boolean;
  activeNavName: string;
}

export const useConfigureStore = defineStore("configure", {
  state: (): ConfigureState => ({
    token: "",
    showAside: false,
    showSongCommentDialog: false,
    activeNavName: "首页",
  }),
  actions: {
    setToken(token: string) {
      this.token = token;
    },
    setActiveNavName(activeNavName: string) {
      this.activeNavName = activeNavName;
    },
    setShowAside(showAside: boolean) {
      this.showAside = showAside;
    },
    setShowSongCommentDialog(showSongCommentDialog: boolean) {
      this.showSongCommentDialog = showSongCommentDialog;
    },
  },
});
