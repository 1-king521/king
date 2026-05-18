import { api, getBaseURL, type ApiResponse } from "@/utils/request";
import { jsonRequestConfig } from "@/api/types";

export interface UserPageData {
  records: Array<{
    id: number;
    username: string;
    email?: string;
    phoneNum?: string;
    avatar?: string;
  }>;
  total: number;
  page: number;
  size: number;
  hasMore: boolean;
}

export async function fetchUserPage(page: number, size: number): Promise<ApiResponse<UserPageData>> {
  return await api<UserPageData>({ url: `user/page?page=${page}&size=${size}` });
}

/** 当前登录用户资料（请求头带 JWT）；登录后请用此接口，勿用 {@link fetchUserPage}（管理端/全量列表，需 admin） */
export async function fetchCurrentUserProfile(): Promise<ApiResponse<UserPageData["records"][0]>> {
  return await api<UserPageData["records"][0]>({ url: "user/profile" });
}

/**
 * 登录接口在签发 JWT 后不再返回用户对象；用当前登录账号在用户分页结果中匹配一条记录。
 * @deprecated 前台登录后请改用 {@link fetchCurrentUserProfile}；分页接口需管理员权限。
 */
export async function fetchUserByLoginAccount(account: string): Promise<UserPageData["records"][0] | null> {
  const trimmed = account.trim();
  if (!trimmed) {
    return null;
  }
  const size = 100;
  let page = 1;
  let more = true;
  while (more) {
    const res = await fetchUserPage(page, size);
    if (!res.success || !res.data?.records) {
      return null;
    }
    const { records, hasMore } = res.data;
    const match = records.find(
      (u) =>
        (u.username && u.username === trimmed) ||
        (u.email && u.email === trimmed) ||
        (u.phoneNum && u.phoneNum === trimmed),
    );
    if (match) {
      return match;
    }
    more = !!hasMore;
    if (more) {
      page += 1;
    }
  }
  return null;
}

export async function fetchUserById(id: string | number): Promise<ApiResponse> {
  return await api({ url: `user/detail?id=${id}` });
}

export async function fetchCollectionByUserId(userId: string | number): Promise<ApiResponse> {
  return await api({ url: `collection/detail?userId=${userId}` });
}

/** 用户最近播放（GET /userPlayHistory/user/{userId}） */
export async function fetchUserPlayHistory(userId: string | number): Promise<ApiResponse> {
  return await api({ url: `userPlayHistory/user/${userId}` });
}

/** 上报播放记录（POST /userPlayHistory/add） */
export async function addUserPlayHistory(
  userId: string | number,
  songId: string | number,
): Promise<ApiResponse> {
  return await api({
    method: "post",
    url: `userPlayHistory/add?userId=${userId}&songId=${songId}`,
  });
}

export async function fetchDeleteCollection(
  userId: string | number,
  songId: string | number,
): Promise<ApiResponse> {
  return await api({
    method: "delete",
    url: `collection/delete?userId=${userId}&songId=${songId}`,
  });
}

export async function fetchSetCollection(payload: {
  userId: string | number;
  type: string;
  songId: string | number;
}): Promise<ApiResponse> {
  return await api({
    method: "post",
    url: "collection/add",
    data: payload,
    config: jsonRequestConfig,
  });
}

export async function fetchCollectionStatus(payload: {
  userId: string | number;
  type: string;
  songId: string | number;
}): Promise<ApiResponse> {
  return await api({
    method: "post",
    url: "collection/status",
    data: payload,
    config: jsonRequestConfig,
  });
}

export async function fetchDeleteSongListCollection(
  userId: string | number,
  songListId: string | number,
): Promise<ApiResponse> {
  return await api({
    method: "delete",
    url: `collection/songList/delete?userId=${userId}&songListId=${songListId}`,
  });
}

export async function fetchSongListCollectionStatus(payload: {
  userId: string | number;
  songListId: string | number;
}): Promise<ApiResponse> {
  return await api({
    method: "post",
    url: "collection/songList/status",
    data: payload,
    config: jsonRequestConfig,
  });
}

export async function fetchSetSongListCollection(payload: {
  userId: string | number;
  songListId: string | number;
}): Promise<ApiResponse> {
  return await api({
    method: "post",
    url: "collection/songList/add",
    data: payload,
    config: jsonRequestConfig,
  });
}

export async function fetchSongListCollectors(songListId: string | number): Promise<ApiResponse> {
  return await api({ url: `collection/songList/collectors?songListId=${songListId}` });
}

export async function fetchDeleteUser(id: string | number): Promise<ApiResponse> {
  return await api({ url: `user/delete?id=${id}` });
}

export async function fetchUpdateUserMsg(payload: {
  id: string | number;
  username: string;
  sex: string | number;
  phoneNum: string;
  email: string;
  birth: string | Date;
  introduction: string;
  location: string;
}): Promise<ApiResponse> {
  return await api({
    method: "post",
    url: "user/update",
    data: payload,
    config: jsonRequestConfig,
  });
}

export async function fetchUpdateUserPassword(payload: {
  id: string | number;
  username: string;
  oldPassword: string;
  password: string;
}): Promise<ApiResponse> {
  return await api({
    method: "post",
    url: "user/updatePassword",
    data: payload,
    config: jsonRequestConfig,
  });
}

export function fetchUploadUrl(userId: string | number): string {
  return `${getBaseURL()}/user/avatar/update?id=${userId}`;
}
