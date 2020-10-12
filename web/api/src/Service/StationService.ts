import { StationModel } from '@/Model/StationModel'

export class StationService {
    private _model: StationModel

    constructor(model: StationModel) {
        this._model = model
    }
}
