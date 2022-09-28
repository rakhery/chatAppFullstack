import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICanal } from '../canal.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../canal.test-samples';

import { CanalService, RestCanal } from './canal.service';

const requireRestSample: RestCanal = {
  ...sampleWithRequiredData,
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
  updatedAt: sampleWithRequiredData.updatedAt?.toJSON(),
};

describe('Canal Service', () => {
  let service: CanalService;
  let httpMock: HttpTestingController;
  let expectedResult: ICanal | ICanal[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CanalService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Canal', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const canal = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(canal).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Canal', () => {
      const canal = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(canal).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Canal', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Canal', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Canal', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCanalToCollectionIfMissing', () => {
      it('should add a Canal to an empty array', () => {
        const canal: ICanal = sampleWithRequiredData;
        expectedResult = service.addCanalToCollectionIfMissing([], canal);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(canal);
      });

      it('should not add a Canal to an array that contains it', () => {
        const canal: ICanal = sampleWithRequiredData;
        const canalCollection: ICanal[] = [
          {
            ...canal,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCanalToCollectionIfMissing(canalCollection, canal);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Canal to an array that doesn't contain it", () => {
        const canal: ICanal = sampleWithRequiredData;
        const canalCollection: ICanal[] = [sampleWithPartialData];
        expectedResult = service.addCanalToCollectionIfMissing(canalCollection, canal);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(canal);
      });

      it('should add only unique Canal to an array', () => {
        const canalArray: ICanal[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const canalCollection: ICanal[] = [sampleWithRequiredData];
        expectedResult = service.addCanalToCollectionIfMissing(canalCollection, ...canalArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const canal: ICanal = sampleWithRequiredData;
        const canal2: ICanal = sampleWithPartialData;
        expectedResult = service.addCanalToCollectionIfMissing([], canal, canal2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(canal);
        expect(expectedResult).toContain(canal2);
      });

      it('should accept null and undefined values', () => {
        const canal: ICanal = sampleWithRequiredData;
        expectedResult = service.addCanalToCollectionIfMissing([], null, canal, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(canal);
      });

      it('should return initial array if no Canal is added', () => {
        const canalCollection: ICanal[] = [sampleWithRequiredData];
        expectedResult = service.addCanalToCollectionIfMissing(canalCollection, undefined, null);
        expect(expectedResult).toEqual(canalCollection);
      });
    });

    describe('compareCanal', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCanal(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCanal(entity1, entity2);
        const compareResult2 = service.compareCanal(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCanal(entity1, entity2);
        const compareResult2 = service.compareCanal(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCanal(entity1, entity2);
        const compareResult2 = service.compareCanal(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
