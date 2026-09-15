export interface PageMetadata {
    size: number;
    totalElements: number;
    totalPages: number;
    number: number;
}

export interface Sort {
    sortField: string;
    sortDirection: 'ASC' | 'DESC';
}

export interface PageResponse<T> {
    content: T[];
    pageable: PageMetadata;
    totalElements: number;
    totalPages: number;
    numberOfElements: number;
    number: number;
    size: number;
    empty: boolean;
    first: boolean;
    last: boolean;
}

