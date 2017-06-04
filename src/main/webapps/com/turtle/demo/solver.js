/**
 * Created by randy on 10/23/2014.
 */

    // Constants
    var charw = ($(document).width()-16)/27,
        charh = charw,//16 441
        halfcharw = Math.floor(charw / 2),
        halfcharh = Math.floor(charh / 2),
        cellw = 3 * charw,
        cellh = 3 * charh,
        puzzlew = 9 * (1 + cellw),
        puzzleh = 9 * (1 + cellh),
    // Puzzle.
        //p = [],
        p2 = [], // share
        candidate = [],
        edit = true,
    // Lookup table.
        tCOL = [],
        tROW = [],
        tBOX = [],

    //
    // Render puzzle.
    //
        ht1 = [],                        // High light pattern.
        ht2 = [],                        // High light helper cell.
        ht3 = [],                        // High light reduction cell.
        ht4 = [],                     // High light links.
        htMask = [],

    //solved
        pattern = [],
        msg,

    //XyChains
        xyMask,
        xyNCell,                           // # 2 candidates cells.
        xyCell = [],                       // 2 candidates cells.
        xyFlag = [],
        xyNChain,                         // Length of XY chain.
        xyChain = [];                     // The XY chain.

    for (var i = 0; i < 81; i++) {
        tCOL[i] = i % 9;
        tROW[i] = Math.floor(i / 9);
        tBOX[i] = 3 * Math.floor(tROW[i] / 3) + Math.floor(tCOL[i] / 3);
    }

    var solver = solver || {

        p:[],

        COL: function (i) {
            return tCOL[i];
        },
        ROW: function (i) {
            return tROW[i];
        },
        BOX: function (i) {
            return tBOX[i];
        },
        //
        // Convert number(1~9) to bit mask.
        //
        n2b: function (n) {
            return 1 << (n - 1);
        },
        //
        // Get number of bit 1s.
        //
        bc: function (n) {
            var c = 0;
            for (var i = 0; i < 9; i++) {
                if (n & (1 << i)) {
                    c += 1;
                }
            }
            return c;
        },
        //
        // Get number from mask.
        //
        b2n: function (mask) {
            for (var i = 0; i < 9; i++) {
                if (mask & (1 << i)) {
                    return 1 + i;
                }
            }
            return 0;
        },
        //
        // Calc the puzzle cell index from col:row.
        //
        getIdxFromColRow:function (col, row) {
            return 9 * row + col;
        },
        //
        // Calc the puzzle cell index from box cell index.
        //
        getIdxFromBoxIdx: function (box, i) {
            var bcol = box % 3;
            var brow = Math.floor(box / 3);
            var ccol = i % 3;
            var crow = Math.floor(i / 3);
            return 9 * (3 * brow + crow) + 3 * bcol + ccol;
        },
        //
        // Is the cell solved?
        //
        isSolved: function (i) {
            return 0 != this.p[i];
        },
        //
        // Does this candidate bitset have only one naked single?
        //
        isSingle: function (candidate) {
            var c = 0, n;
            for (var i = 0; i < 9; i++) {
                if (candidate & (1 << i)) {
                    c += 1;
                    n = i + 1;
                }
            }
            if (1 == c) {
                return n;
            } else {
                return 0;
            }
        },

        //
        // Get the candidate list of a box.
        //

        getCandidateListOfBox: function (box, c, idx) {
            for (var cell = 0; cell < 9; cell++) {
                var i = this.getIdxFromBoxIdx(box, cell);
                c[cell] = this.isSolved(i) ? 0 : candidate[i];
                idx[cell] = i;
            }
        },

        //
        // Get the candidate list of a col.
        //

        getCandidateListOfCol: function (col, c, idx) {
            for (var row = 0; row < 9; row++) {
                var i = this.getIdxFromColRow(col, row);
                c[row] = this.isSolved(i) ? 0 : candidate[i];
                idx[row] = i;
            }
        },

        //
        // Get the candidate list of a row.
        //

        getCandidateListOfRow: function (row, c, idx) {
            for (var col = 0; col < 9; col++) {
                var i = this.getIdxFromColRow(col, row);
                c[col] = this.isSolved(i) ? 0 : candidate[i];
                idx[col] = i;
            }
        },

        //
        // Count how many n exist in the candidates list.
        //

        getCandidateCountOfList: function (candidate, n, cell) {
            var c = 0;
            for (var i = 0; i < 9; i++) {
                if (candidate[i] & this.n2b(n)) {
                    cell[c++] = i;
                }
            }
            return c;
        },

        //
        // Init candidate bitset of each puzzle cell.
        //

        initCandidates: function () {

            for (var row = 0; row < 9; row++) {
                for (var col = 0; col < 9; col++) {
                    var i = this.getIdxFromColRow(col, row);
                    if (!this.isSolved(i)) {
                        candidate[i] = 0x1ff;
                    } else {
                        candidate[i] = this.n2b(this.p[i]);
                    }
                }
            }

            //
            // Update candidates of puzzle cells by current state.
            //

            this.updateCandidates();
        },

        updateCandidates: function () {
            //
            // Update candidates in a box.
            //
            for (var box = 0; box < 9; box++) {
                for (var cell = 0; cell < 9; cell++) {

                    var index = this.getIdxFromBoxIdx(box, cell);
                    if (!this.isSolved(index)) {
                        continue;
                    }

                    //
                    // Removed this solved number from from other cells candidate in this
                    // box.
                    //

                    var n = this.p[index];
                    for (var i = 0; i < 9; i++) {
                        if (i != cell) {
                            candidate[this.getIdxFromBoxIdx(box, i)] &= ~this.n2b(n);
                        }
                    }
                }
            }
            //
            // Update candidates in col.
            //
            for (var col = 0; col < 9; col++) {
                for (var row = 0; row < 9; row++) {

                    var index = this.getIdxFromColRow(col, row);
                    if (!this.isSolved(index)) {
                        continue;
                    }

                    //
                    // Removed this solved number from from other cells candidate in this
                    // col.
                    //

                    var n = this.p[index];
                    for (var i = 0; i < 9; i++) {
                        if (i != row) {
                            candidate[this.getIdxFromColRow(col, i)] &= ~this.n2b(n);
                        }
                    }
                }
            }
            //
            // Update candidates in row.
            //
            for (var row = 0; row < 9; row++) {
                for (var col = 0; col < 9; col++) {

                    var index = this.getIdxFromColRow(col, row);
                    if (!this.isSolved(index)) {
                        continue;
                    }

                    //
                    // Removed this solved number from from other cells candidate in this
                    // row.
                    //

                    var n = this.p[index];
                    for (var i = 0; i < 9; i++) {
                        if (i != col) {
                            candidate[this.getIdxFromColRow(i, row)] &= ~this.n2b(n);
                        }
                    }
                }
            }
        },

        fillCell: function (ctx, x, y, color) {
            ctx.fillStyle = color;
            ctx.fillRect(x, y, cellw + 1, cellh + 1);
            ctx.fillStyle = 'Black';
        },

        renderPuzzle: function (name) {
            // console.log('name',name);
            var c = document.getElementById(name);
            var ctx = c.getContext('2d');//$("body")[0].getContext('2d'); //
            ctx.lineWidth = 1;
            ctx.textAlign = "center";
            ctx.textBaseline = "middle";

            ctx.fillStyle = '#FFFFFF'; //Ivory
            ctx.fillRect(0, 0, puzzlew, puzzleh);
            ctx.fillStyle = 'Black';
            //
            // Draw cells.
            //
            for (var i = 0; i < 81; i++) {

                var x = this.COL(i) * (1 + cellw), y = this.ROW(i) * (1 + cellh);

                if (-1 != ht1.indexOf(i)) {
                    this.fillCell(ctx, x, y, '#B6FF00');
                } else if (-1 != ht2.indexOf(i)) {
                    this.fillCell(ctx, x, y, '#F8FF90');
                }

                x += Math.floor(charw / 2);
                y += Math.floor(charh / 2) + 1;

                if (0 != this.p[i]) {

                    //
                    // Draw fixed cells.
                    //

                    ctx.font = '30px Arial';
                    ctx.fillText(this.p[i], x + charw, y + charh);

                } else {

                    //
                    // Draw candidates.
                    //

                    ctx.font = charh + 'px sans-serif';
                    for (var j = 0; j < 9; j++) {
                        if (candidate[i] & this.n2b(1 + j)) {
                            ctx.fillText(1 + j, x + (j % 3) * charw, y + Math.floor(j / 3) * charh);
                        }
                    }

                    var ht = ht3.indexOf(i);
                    if (-1 != ht && 0 != htMask[ht]) {
                        ctx.fillStyle = 'Red';
                        for (var j = 0; j < 9; j++) {
                            if (htMask[ht] & this.n2b(1 + j)) {
                                var cx = x + (j % 3) * charw, cy = y + Math.floor(j / 3) * charh;
                                ctx.fillText(1 + j, cx, cy);
                                ctx.moveTo(cx - halfcharw, cy - halfcharh);
                                ctx.lineTo(cx + halfcharw, cy + halfcharh);
                                ctx.stroke();
                            }
                        }
                        ctx.fillStyle = 'Black';
                    }
                }
            }

            //
            // Draw grids.
            //

            for (var i = 1; i < 9; i++) {
                if (0 == (i % 3)) {
                    ctx.strokeStyle = 'Black';
                } else {
                    ctx.strokeStyle = 'LightGray';
                }
                var x = i * (1 + cellw),
                    y = i * (1 + cellh);
                ctx.beginPath();
                ctx.moveTo(x, 0);
                ctx.lineTo(x, puzzleh);
                ctx.moveTo(0, y);
                ctx.lineTo(puzzlew, y);
                ctx.stroke();
            }

            //
            // Draw links.
            //

            ctx.strokeStyle = 'Red';
            for (var i = 0; i < ht4.length; i += 3) {
                var a = ht4[i], b = ht4[i + 1];
                var n = ht4[i + 2] - 1;
                var xa = this.COL(a) * (1 + cellw), ya = this.ROW(a) * (1 + cellh);
                var cxa = xa + (n % 3) * charw, cya = ya + Math.floor(n / 3) * charh;
                var xb = this.COL(b) * (1 + cellw), yb = this.ROW(b) * (1 + cellh);
                var cxb = xb + (n % 3) * charw, cyb = yb + Math.floor(n / 3) * charh;
                ctx.beginPath();
                ctx.rect(cxa, cya, charw, charh);
                ctx.rect(cxb, cyb, charw, charh);
                ctx.moveTo(cxa + halfcharw, cya + halfcharh);
                ctx.lineTo(cxb + halfcharw, cyb + halfcharh);
                ctx.stroke();
            }
            ctx.strokeStyle = 'Black';

            //
            // Reset high lights.
            //

            ht1 = [];
            ht2 = [];
            ht3 = [];
            ht4 = [];
            htMask = [];
        },

        //
        // New game.
        //

        resetPuzzle:function () {

            //
            // Enable edit.
            //

            edit = true;

            //sharelink.style.display = 'none';

            //
            // Clear screen.
            //

            var s = document.getElementById('solver-steps');
            if (s) {
                document.body.removeChild(s);
            }

            s = document.createElement('div');
            s.setAttribute('id', 'solver-steps');
            document.body.appendChild(s);

            //
            // Rest puzzle.
            //

            for (var i = 0; i < 81; i++) {
                p[i] = 0;
            }

            this.initCandidates();

            //
            // Draw puzzle.
            //

            this.renderPuzzle('s0');
        },

        //
        // Add solve step.
        //

        addStep: function (round) {

            var name = 's' + round;

            //
            // Create canvas.
            //
            var steps = document.getElementById('solver-steps');

            var h = document.createElement('h2');
            h.appendChild(document.createTextNode(round + ', ' + msg));
            steps.appendChild(h);
            var c = document.createElement('canvas');
            c.setAttribute('id', name);
            c.setAttribute('width', puzzlew);
            c.setAttribute('height', puzzleh);
            steps.appendChild(c);

            //
            // Draw puzzle.
            //

            this.renderPuzzle(name);
        },

        solve: function () {
            if(pattern.length==0) {
                pattern.push(this.p_findSingle);
                pattern.push(this.p_findClaiming);
                pattern.push(this.p_findPointing);
                pattern.push(this.p_findSubset);
                pattern.push(this.p_findXWings);
                pattern.push(this.p_findXyWings);
                pattern.push(this.p_findWWings);
                pattern.push(this.p_findXyzWings);
                pattern.push(this.p_findXChains);
                pattern.push(this.p_findXyChains);
            }

            //console.warn('pattern',pattern);
            //sharelink.style.display = 'none';
            //if (!edit) {
            //    return;
            //}
            p2 = this.p.slice(0);                    // Backup original puzzle. 相当于 split(',')
            var round = 1;
            while (true) {
                var over = true;
                // console.log('p',p);
                for (var i = 0; i < pattern.length; i++) {
                    if (pattern[i](round)) {
                        this.updateCandidates();
                        this.addStep(round++);
                        if (comm.isSolverAll) {
                            over = false
                        } else if (msg == comm.tech.NakedSingle || msg == comm.tech.HiddenSingle) {
                            over = true
                        } else {
                            over = false;
                        }

                        edit = false;
                        break;
                    }
                }
                if (over) {
                    break;
                }
            }
        },
        //
        // Find single candidate in a candidate list.
        //

        findSingle: function (c, idx, round) {

            var cell = [];

            for (var n = 1; n <= 9; n++) {

                if (1 != this.getCandidateCountOfList(c, n, cell)) {
                    continue;
                }

                var i = idx[cell[0]];

                this.p[i] = n;
                candidate[i] = this.n2b(n);

                ht1.push(i);

                return true;
            }

            return false;
        },

        p_findSingle: function (round) {

            //
            // Find hidden single.
            //

            msg = comm.tech.HiddenSingle;//'Hidden Single';

            var c = [], idx = [];
            for (var i = 0; i < 9; i++) {

                solver.getCandidateListOfBox(i, c, idx);
                if (solver.findSingle(c, idx, round)) {
                    ht2 = idx;
                    return true;
                }

                solver.getCandidateListOfCol(i, c, idx);
                if (solver.findSingle(c, idx, round)) {
                    ht2 = idx;
                    return true;
                }

                solver.getCandidateListOfRow(i, c, idx);
                if (solver.findSingle(c, idx, round)) {
                    ht2 = idx;
                    return true;
                }
            }

            //
            // Find naked single.
            //

            for (var i = 0; i < 9 * 9; i++) {

                if (solver.isSolved(i)) {
                    continue;
                }

                var n = solver.isSingle(candidate[i]);
                if (0 == n) {
                    continue;
                }

                solver.p[i] = n;
                candidate[i] = solver.n2b(n);

                ht1.push(i);

                msg = comm.tech.NakedSingle; //'Naked Single';
                return true;
            }

            return false;
        },

        findClaiming: function (c, idx, round) {

            //
            // Find claiming pattern in a col or a row.
            //

            var cell = [];

            for (var n = 1; n <= 9; n++) {

                //
                // 1, there are only 2 or 3 candidates found, so it is possible in the
                // same box.
                //

                var count = solver.getCandidateCountOfList(c, n, cell);
                if (2 != count && 3 != count) {
                    continue;
                }

                if (2 == count) {
                    cell[2] = cell[0];
                }

                //
                // 2, checks are theses candidates in the same box.
                //

                if (solver.BOX(idx[cell[0]]) != solver.BOX(idx[cell[1]]) ||
                    solver.BOX(idx[cell[0]]) != solver.BOX(idx[cell[2]])) {
                    continue;
                }

                //
                // Pattern found, remove extra candidates.
                //

                var c2 = [], idx2 = [];
                solver.getCandidateListOfBox(solver.BOX(idx[cell[0]]), c2, idx2);

                var changed = false;

                var mask = solver.n2b(n);
                for (var i = 0; i < 9; i++) {

                    var index = idx2[i];
                    if (solver.isSolved(index)) {
                        continue;
                    }

                    var again = false;
                    for (var j = 0; j < count; j++) {
                        if (index == idx[cell[j]]) {
                            again = true;
                            break;
                        }
                    }
                    if (again) {
                        continue;
                    }

                    if (0 == (candidate[index] & mask)) {
                        continue;
                    }

                    candidate[index] &= ~mask;
                    changed = true;

                    ht2 = idx;
                    ht3.push(index);
                    htMask.push(mask);
                }

                if (!changed) {
                    continue;
                }

                //
                // State changed.
                //

                for (var i = 0; i < count; i++) {
                    ht1.push(idx[cell[i]])
                }

                msg = comm.tech.Claiming;// 'Claiming';
                return true;
            }

            return false;
        },

        p_findClaiming: function (round) {

            //
            // Find claiming pattern.
            //

            var c = [], idx = [];

            for (var i = 0; i < 9; i++) {

                solver.getCandidateListOfCol(i, c, idx);
                if (solver.findClaiming(c, idx, round)) {
                    return true;
                }

                solver.getCandidateListOfRow(i, c, idx);
                if (solver.findClaiming(c, idx, round)) {
                    return true;
                }
            }

            return false;
        },

        p_findPointing: function (round) {

            //
            // Find pointing pattern in a box.
            //

            var c = [], idx = [], cell = [];

            for (var box = 0; box < 9; box++) {

                solver.getCandidateListOfBox(box, c, idx);

                for (var n = 1; n <= 9; n++) {

                    //
                    // 1, there are must only 2 or 3 candidates in this box.
                    //

                    var count = solver.getCandidateCountOfList(c, n, cell);
                    if (2 != count && 3 != count) {
                        continue;
                    }

                    //
                    // 2, check are these candidates in the same col or row.
                    //

                    var col = [], row = [];
                    for (var i = 0; i < count; i++) {
                        var index = idx[cell[i]];
                        col[i] = solver.COL(index) % 3;
                        row[i] = solver.ROW(index) % 3;
                    }

                    if (2 == count) {
                        col[2] = col[0];
                        row[2] = row[0];
                    }

                    var c2 = [], idx2 = [];
                    if (col[0] == col[1] && col[0] == col[2]) {
                        solver.getCandidateListOfCol(solver.COL(idx[cell[0]]), c2, idx2);
                    } else if (row[0] == row[1] && row[0] == row[2]) {
                        solver.getCandidateListOfRow(solver.ROW(idx[cell[0]]), c2, idx2);
                    } else {
                        continue;
                    }

                    //
                    // Pattern found, remove extra candidates.
                    //

                    var changed = false;

                    var mask = solver.n2b(n);
                    for (var i = 0; i < 9; i++) {

                        var index = idx2[i];
                        if (solver.isSolved(index)) {
                            continue;
                        }

                        var again = false;
                        for (var j = 0; j < count; j++) {
                            if (index == idx[cell[j]]) {
                                again = true;
                            }
                        }
                        if (again) {
                            continue;
                        }

                        if (0 == (candidate[index] & mask)) {
                            continue;
                        }

                        candidate[index] &= ~mask;
                        changed = true;

                        ht2 = idx;
                        ht3.push(index);
                        htMask.push(mask);
                    }

                    if (!changed) {
                        continue;
                    }

                    //
                    // State changed.
                    //

                    for (var i = 0; i < count; i++) {
                        ht1.push(idx[cell[i]]);
                    }

                    msg = comm.tech.Pointing;//'Pointing';
                    return true;
                }
            }

            return false;
        },

        findNakedSet: function (c, idx, n, round) {

            //
            // Find naked subset.
            //

            var pos = [];

            for (var i = 0; i < 0x1ff; i++) {

                //
                // Use bit count technique to generate bitset pattern.
                //

                if (solver.bc(i) != n) {
                    continue;
                }

                //
                // Find is there exact n cells in the list that each cell contains
                // partial or all bits of generated bitset.
                //

                var i2 = 0;
                for (var j = 0; j < 9; j++) {
                    if (c[j] && 0 == (c[j] & ~i)) {
                        pos[i2++] = idx[j];
                    }
                }

                if (i2 != n) {
                    continue;
                }

                //
                // Pattern found, remove extra candidates.
                //

                var changed = false;

                for (var j = 0; j < 9; j++) {

                    var index = idx[j];
                    if (solver.isSolved(index)) {
                        continue;
                    }

                    var again = false;
                    for (var k = 0; k < n; k++) {
                        if (index == pos[k]) {
                            again = true;
                            break;
                        }
                    }
                    if (again) {
                        continue;
                    }

                    if (0 == (candidate[idx[j]] & i)) {
                        continue;
                    }

                    htMask.push(candidate[idx[j]] & i);
                    ht3.push(idx[j]);

                    candidate[idx[j]] &= ~i;
                    changed = true;
                }

                if (!changed) {
                    continue;
                }

                //
                // State changed.
                //

                for (var j = 0; j < n; j++) {
                    ht1.push(pos[j]);
                }

                ht2 = idx;

                msg = comm.tech.NakedSubset;//'Naked Subset';
                return true;
            }

            return false;
        },

        findHiddenSet: function (c, idx, n, round) {

            //
            // Gather the distributions of numbers 1~9.
            //

            var count = [];

            var cell = [];
            for (var i = 0; i < 9; i++) {
                cell[i] = [];
            }

            for (var i = 0; i < 9; i++) {
                count[i] = solver.getCandidateCountOfList(c, 1 + i, cell[i]);
            }

            //
            // 1, check how many numbers fit in the range n. The count must equal to n,
            // and this will be the potential subset.
            //

            var nset = 0;
            var set = [];

            for (var i = 0; i < 9; i++) {
                if (0 != count[i] && count[i] <= n) {
                    set[nset++] = i;
                }
            }

            if (nset != n) {
                return false;
            }

            //
            // 2, find how many cells occupied by these potential subset numbers.
            // The count should exact equal to the subset length(n).
            //

            var npos = 0;
            var pos = [];

            for (var i = 0; i < nset; i++) {
                for (var j = 0; j < count[set[i]]; j++) {
                    if (0 != npos) {
                        var found = false;
                        for (var k = 0; k < npos; k++) {
                            if (pos[k] == cell[set[i]][j]) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            pos[npos++] = cell[set[i]][j];
                        }
                    } else {
                        pos[npos++] = cell[set[i]][j];
                    }
                }
            }

            if (npos != n) {
                return false;
            }

            //
            // Pattern found, remove extra candidates.
            //

            var change = false;

            for (var i = 0; i < 9; i++) {

                var index = idx[i];
                if (solver.isSolved(index)) {
                    continue;
                }

                for (var j = 0; j < npos; j++) {
                    if (i == pos[j]) {

                        var mask = 0;
                        for (var k = 0; k < nset; k++) {
                            mask |= solver.n2b(1 + set[k]);
                        }

                        var prev = candidate[index];
                        candidate[index] &= mask;
                        if (prev != candidate[index]) {
                            change = true;
                            htMask.push(prev ^ mask);
                        }

                        break;
                    }
                }
            }

            if (!change) {
                return false;
            }

            //
            // State changed.
            //

            for (var i = 0; i < n; i++) {
                ht1.push(idx[pos[i]]);
            }

            ht2 = idx;
            ht3 = ht1;

            msg = comm.tech.HiddenSubset;//'Hidden Subset';
            return true;
        },

        p_findSubset: function (round) {

            //
            // Find subset pattern.
            //

            var c = [], idx = [];

            for (var n = 2; n <= 4; n++) {
                for (var i = 0; i < 9; i++) {

                    solver.getCandidateListOfBox(i, c, idx);
                    if (solver.findNakedSet(c, idx, n, round)) {
                        return true;
                    }

                    solver.getCandidateListOfCol(i, c, idx);
                    if (solver.findNakedSet(c, idx, n, round)) {
                        return true;
                    }

                    solver.getCandidateListOfRow(i, c, idx);
                    if (solver.findNakedSet(c, idx, n, round)) {
                        return true;
                    }
                }
            }

            for (var n = 2; n <= 4; n++) {
                for (var i = 0; i < 9; i++) {

                    solver.getCandidateListOfBox(i, c, idx);
                    if (solver.findHiddenSet(c, idx, n, round)) {
                        return true;
                    }

                    solver.getCandidateListOfCol(i, c, idx);
                    if (solver.findHiddenSet(c, idx, n, round)) {
                        return true;
                    }

                    solver.getCandidateListOfRow(i, c, idx);
                    if (solver.findHiddenSet(c, idx, n, round)) {
                        return true;
                    }
                }
            }

            return false;
        },

        findXWings: function (round, isRow) {

            //
            // Find X Wing pattern.
            //

            var c = [], idx = [], cell = [];
            var c2 = [], idx2 = [], cell2 = [];
            var c34 = [], idx34 = [];
            for (var i = 0; i < 2; i++) {
                c34[i] = [];
                idx34[i] = [];
            }

            for (var n = 1; n <= 9; n++) {

                for (var i = 0; i < 9; i++) {

                    //
                    // X wings only contain 2 candidates.
                    //

                    if (isRow) {
                        solver.getCandidateListOfRow(i, c, idx);
                    } else {
                        solver.getCandidateListOfCol(i, c, idx);
                    }

                    if (2 != solver.getCandidateCountOfList(c, n, cell)) {
                        continue;
                    }

                    for (var j = 1 + i; j < 9; j++) {

                        //
                        // X wings only contain 2 candidates.
                        //

                        if (isRow) {
                            solver.getCandidateListOfRow(j, c2, idx2);
                        } else {
                            solver.getCandidateListOfCol(j, c2, idx2);
                        }

                        if (2 != solver.getCandidateCountOfList(c2, n, cell2)) {
                            continue;
                        }

                        //
                        // 4 cells should in the same row or col.
                        //

                        if (cell[0] != cell2[0] || cell[1] != cell2[1]) {
                            continue;
                        }

                        var changed = false;

                        if (isRow) {
                            solver.getCandidateListOfCol(cell[0], c34[0], idx34[0]);
                            solver.getCandidateListOfCol(cell[1], c34[1], idx34[1]);
                        } else {
                            solver.getCandidateListOfRow(cell[0], c34[0], idx34[0]);
                            solver.getCandidateListOfRow(cell[1], c34[1], idx34[1]);
                        }

                        for (var l = 0; l < 2; l++) {
                            for (var k = 0; k < 9; k++) {

                                if (i == k || j == k || solver.isSolved(idx34[l][k])) {
                                    continue;
                                }

                                if (c34[l][k] & solver.n2b(n)) {
                                    changed = true;
                                    candidate[idx34[l][k]] &= ~solver.n2b(n);
                                    ht3.push(idx34[l][k]);
                                    htMask.push(this.n2b(n));
                                }
                            }
                        }

                        if (!changed) {
                            continue;
                        }

                        //
                        // State changed.
                        //

                        ht1.push(idx[cell[0]]);
                        ht1.push(idx[cell[1]]);
                        ht1.push(idx2[cell[0]]);
                        ht1.push(idx2[cell[1]]);

                        ht2 = idx.concat(idx2).concat(idx34[0]).concat(idx34[1]);

                        ht4.push(idx[cell[0]]);
                        ht4.push(idx2[cell[1]]);
                        ht4.push(n);
                        ht4.push(idx[cell[1]]);
                        ht4.push(idx2[cell[0]]);
                        ht4.push(n);

                        msg = comm.tech.X_Wing;//'X-Wings';
                        return true;
                    }
                }
            }

            return false;
        },

        p_findXWings: function (round) {

            //
            // Find X Wing pattern.
            //

            return solver.findXWings(round, true) || solver.findXWings(round, false);
        },

        findXyWings1: function (round) {

            //
            // Find XY Wings type 1. 3 cells in 3 boxs.
            //

            for (var i = 0; i < 81; i++) {

                //
                // Each wing only contains 2 candidates.
                //

                if (solver.isSolved(i) || 2 != solver.bc(candidate[i])) {
                    continue;
                }

                var box = solver.BOX(i);
                var c = [], idx = [], c2 = [], idx2 = [];
                var w = [];

                solver.getCandidateListOfRow(solver.ROW(i), c, idx);

                for (var j = 0; j < 9; j++) {

                    if (idx[j] == i || solver.isSolved(idx[j]) ||
                        2 != solver.bc(c[j]) || 0 == (c[j] & candidate[i]) ||
                        solver.BOX(idx[j]) == box) {
                        continue;
                    }

                    w[0] = idx[j];

                    solver.getCandidateListOfCol(solver.COL(i), c2, idx2);

                    for (var k = 0; k < 9; k++) {

                        if (idx2[k] == i || solver.isSolved(idx2[k]) ||
                            2 != solver.bc(c2[k]) || candidate[w[0]] != (c2[k] ^ candidate[i]) ||
                            solver.BOX(idx2[k]) == box) {
                            continue;
                        }

                        w[1] = idx2[k];

                        if (solver.BOX(w[0]) == solver.BOX(w[1])) {
                            continue;
                        }

                        var i3 = solver.getIdxFromColRow(solver.COL(w[0]), solver.ROW(w[1]));
                        if (solver.isSolved(i3)) {
                            continue;
                        }

                        var mask = candidate[w[0]] & candidate[w[1]];
                        if (0 == (candidate[i3] & mask)) {
                            continue;
                        }

                        //
                        // State changed.
                        //

                        candidate[i3] &= ~mask;

                        ht1.push(i);
                        ht1.push(w[0]);
                        ht1.push(w[1]);

                        ht3.push(i3);
                        htMask.push(mask);

                        msg = comm.tech.XY_Wing;// 'XY-Wings';
                        return true;
                    }
                }
            }

            return false;
        },

        findXyWings2: function (round) {

            //
            // Find XY Wings type 2. 2 cells in one box.
            //

            var c = [], idx = [], c2 = [], idx2 = [];

            for (var box = 0; box < 9; box++) {

                solver.getCandidateListOfBox(box, c, idx);

                for (var cell = 0; cell < 9; cell++) {

                    //
                    // Each wing only contains 2 candidates.
                    //

                    if (solver.isSolved(idx[cell]) || 2 != solver.bc(c[cell])) {
                        continue;
                    }

                    for (var cell2 = 0; cell2 < 9; cell2++) {

                        if (cell2 == cell || solver.isSolved(idx[cell2]) || 2 != solver.bc(c[cell2])) {
                            continue;
                        }

                        if (0 == (c[cell] & c[cell2])) {
                            continue;
                        }

                        //
                        // Find 3rd cell.
                        //

                        var col = solver.COL(idx[cell]), row = solver.ROW(idx[cell]);
                        var col2 = solver.COL(idx[cell2]), row2 = solver.ROW(idx[cell2]);

                        var x = c[cell] ^ c[cell2];
                        if (0 == x) {
                            continue;
                        }

                        var i3, mask;

                        var changed = false;

                        if (col != col2) {

                            solver.getCandidateListOfCol(col, c2, idx2);

                            for (var i = 0; i < 9; i++) {

                                if (solver.isSolved(idx2[i]) || 2 != solver.bc(c2[i]) ||
                                    c2[i] != x || solver.BOX(idx2[i]) == box) {
                                    continue;
                                }

                                //
                                // 3rd cell found.
                                //

                                i3 = solver.getIdxFromColRow(col, Math.floor(idx2[i] / 9));

                                mask = c2[i] & c[cell2];
                                for (var j = 0; j < 9; j++) {
                                    if (!solver.isSolved(idx2[j]) && Math.floor(idx2[j] / 9) != row &&
                                        0 != (c2[j] & mask) && solver.BOX(idx2[j]) == box) {
                                        changed = true;
                                        candidate[idx2[j]] &= ~mask;
                                        ht3.push(idx2[j]);
                                        htMask.push(mask);
                                    }
                                }

                                solver.getCandidateListOfCol(col2, c2, idx2);

                                for (var j = 0; j < 9; j++) {
                                    if (!solver.isSolved(idx2[j]) && 0 != (c2[j] & mask) &&
                                        solver.BOX(idx2[j]) == solver.BOX(i3)) {
                                        changed = true;
                                        candidate[idx2[j]] &= ~mask;
                                        ht3.push(idx2[j]);
                                        htMask.push(mask);
                                    }
                                }

                                break;
                            }
                        }

                        if (!changed && row != row2) {

                            solver.getCandidateListOfRow(row, c2, idx2);

                            for (var i = 0; i < 9; i++) {

                                if (solver.isSolved(idx2[i]) || 2 != solver.bc(c2[i]) ||
                                    c2[i] != x || solver.BOX(idx2[i]) == box) {
                                    continue;
                                }

                                //
                                // 3rd cell found.
                                //

                                i3 = solver.getIdxFromColRow(solver.COL(idx2[i]), row);

                                mask = c2[i] & c[cell2];
                                for (var j = 0; j < 9; j++) {
                                    if (!solver.isSolved(idx2[j]) && solver.COL(idx2[j]) != col &&
                                        0 != (c2[j] & mask) && solver.BOX(idx2[j]) == box) {
                                        changed = true;
                                        candidate[idx2[j]] &= ~mask;
                                        ht3.push(idx2[j]);
                                        htMask.push(mask);
                                    }
                                }

                                solver.getCandidateListOfRow(row2, c2, idx2);

                                for (var j = 0; j < 9; j++) {
                                    if (!solver.isSolved(idx2[j]) && 0 != (c2[j] & mask) &&
                                        solver.BOX(idx2[j]) == solver.BOX(i3)) {
                                        changed = true;
                                        candidate[idx2[j]] &= ~mask;
                                        ht3.push(idx2[j]);
                                        htMask.push(mask);
                                    }
                                }

                                break;
                            }
                        }

                        if (!changed) {
                            continue;
                        }

                        //
                        // State changed.
                        //

                        ht1.push(idx[cell]);
                        ht1.push(idx[cell2]);
                        ht1.push(i3);

                        msg = comm.tech.XY_Wing;// 'XY-Wings';
                        return true;
                    }
                }
            }

            return false;
        },

        p_findXyWings: function (round) {

            //
            // Find XY Wings pattern.
            //

            return solver.findXyWings1(round) || solver.findXyWings2(round);
        },

        isChanged: function (a, b, mask) {
            var changed = false;
            var c = [], idx = [];

            if (solver.COL(a) == solver.COL(b)) {
                solver.getCandidateListOfCol(solver.COL(a), c, idx);
                for (var k = 0; k < 9; k++) {
                    if (idx[k] != a && idx[k] != b && !solver.isSolved(idx[k]) && c[k] & mask) {
                        changed = true;
                        candidate[idx[k]] &= ~mask;
                        ht3.push(idx[k]);
                        htMask.push(mask);
                    }
                }
            } else if (solver.ROW(a) == solver.ROW(b)) {
                solver.getCandidateListOfRow(solver.ROW(a), c, idx);
                for (var k = 0; k < 9; k++) {
                    if (idx[k] != a && idx[k] != b && !solver.isSolved(idx[k]) && c[k] & mask) {
                        changed = true;
                        candidate[idx[k]] &= ~mask;
                        ht3.push(idx[k]);
                        htMask.push(mask);
                    }
                }
            } else {
                solver.getCandidateListOfBox(solver.BOX(a), c, idx);
                for (var k = 0; k < 9; k++) {
                    if (idx[k] != a && !solver.isSolved(idx[k]) && c[k] & mask &&
                        (solver.ROW(b) == solver.ROW(idx[k]) || solver.COL(b) == solver.COL(idx[k]))) {
                        changed = true;
                        candidate[idx[k]] &= ~mask;
                        ht3.push(idx[k]);
                        htMask.push(mask);
                    }
                }

                solver.getCandidateListOfBox(solver.BOX(b), c, idx);
                for (var k = 0; k < 9; k++) {
                    if (idx[k] != b && !solver.isSolved(idx[k]) && c[k] & mask &&
                        (solver.ROW(a) == solver.ROW(idx[k]) || solver.COL(a) == solver.COL(idx[k]))) {
                        changed = true;
                        candidate[idx[k]] &= ~mask;
                        ht3.push(idx[k]);
                        htMask.push(mask);
                    }
                }

                var ix1 = solver.getIdxFromColRow(COL(a), solver.ROW(b));
                if (!solver.isSolved(ix1) && candidate[ix1] & mask) {
                    changed = true;
                    candidate[ix1] &= ~mask;
                    ht3.push(ix1);
                    htMask.push(mask);
                }

                var ix2 = solver.getIdxFromColRow(solver.COL(b), solver.ROW(a));
                if (!solver.isSolved(ix2) && candidate[ix2] & mask) {
                    changed = true;
                    candidate[ix2] &= ~mask;
                    ht3.push(ix2);
                    htMask.push(mask);
                }
            }

            return changed;
        },

        findWWings: function (c, idx, i1, i2, round) {

            var r1 = solver.ROW(i1), c1 = solver.COL(i1);
            var r2 = solver.ROW(i2), c2 = solver.COL(i2);

            if (r1 == r2 || c1 == c2) {
                return false;
            }

            var cell = [];
            for (var i = 0; i < 9; i++) {

                //
                // This number is one of the wing.
                //

                if (0 == (solver.n2b(1 + i) & candidate[i1])) {
                    continue;
                }

                //
                // There are only 2 candidates in this set that makes a strong chain.
                //

                if (2 != solver.getCandidateCountOfList(c, 1 + i, cell)) {
                    continue;
                }

                //
                // The chain is not i1 or i2.
                //

                if (idx[cell[0]] == i1 || idx[cell[0]] == i2 ||
                    idx[cell[1]] == i1 || idx[cell[1]] == i2) {
                    continue;
                }

                //
                // Make sure the chain links to i1 and i2.
                //

                var lr1 = solver.ROW(idx[cell[0]]), lc1 = solver.COL(idx[cell[0]]);
                var lr2 = solver.ROW(idx[cell[1]]), lc2 = solver.COL(idx[cell[1]]);

                if ((r1 != lr1 || r2 != lr2) && (c1 != lc1 || c2 != lc2) &&
                    (r1 != lr2 || r2 != lr1) && (c1 != lc2 || c2 != lc1)) {
                    continue;
                }

                //
                // Check changes.
                //

                if (!solver.isChanged(i1, i2, candidate[i1] & ~solver.n2b(1 + i))) {
                    continue;
                }

                ht1.push(i1);
                ht1.push(i2);
                ht2.push(solver.getIdxFromColRow(lc1, lr1));
                ht2.push(solver.getIdxFromColRow(lc2, lr2));

                msg = comm.tech.W_Wing;// 'W-Wings';
                return true;
            }

            return false;
        },

        p_findWWings: function (round) {

            //
            // Find W Wings pattern.
            //

            for (var i = 0; i < 81; i++) {

                //
                // 1st, find a cell that contains only 2 candidates.
                //

                if (solver.isSolved(i) || 2 != solver.bc(candidate[i])) {
                    continue;
                }

                var box1 = solver.BOX(i);

                //
                // 2nd, find another cell that contains 2 same candidates with 1st cell.
                //

                for (var j = i + 1; j < 81; j++) {

                    if (solver.isSolved(j) || candidate[i] != candidate[j]) {
                        continue;
                    }

                    var box2 = solver.BOX(j);

                    if (box1 == box2) {
                        continue;
                    }

                    //
                    // 3rd, for each row/col/box, find a single chain to link these 2 cells.
                    //

                    var c = [], idx = [];

                    for (var k = 0; k < 9; k++) {

                        solver.getCandidateListOfCol(k, c, idx);
                        if (solver.findWWings(c, idx, i, j, round)) {
                            return true;
                        }

                        solver.getCandidateListOfRow(k, c, idx);
                        if (solver.findWWings(c, idx, i, j, round)) {
                            return true;
                        }

                        solver.getCandidateListOfBox(k, c, idx);
                        if (solver.findWWings(c, idx, i, j, round)) {
                            return true;
                        }
                    }
                }
            }

            return false;
        },

        findXyzWings: function (c, idx, box, isRow, rowcol, i1, i2, c1, c2, mask) {

            var x = c1 ^ c2;
            var changed = false;

            for (var i = 0; i < 9; i++) {

                if (solver.isSolved(idx[i])) {
                    continue;
                }

                //
                // 3rd cell must has 2 candidates and in different box to c1/c2.
                //

                if (2 != solver.bc(c[i]) || 0 == (c[i] & x) || 0 == (c1 & c[i]) ||
                    2 != solver.bc(c1 & c[i]) || solver.BOX(idx[i]) == box) {
                    continue;
                }

                //
                // 3rd cell found.
                //

                var i3 = idx[i];

                mask = c[i] & c2;
                for (var j = 0; j < 9; j++) {
                    if (!solver.isSolved(idx[j]) && i1 != idx[j] && i2 != idx[j] &&
                        ((isRow && solver.ROW(idx[j]) != rowcol) || (solver.COL(idx[j]) != rowcol)) &&
                        0 != (c[j] & mask) && solver.BOX(idx[j]) == box) {
                        changed = true;
                        candidate[idx[j]] &= ~mask;
                        ht3.push(idx[j]);
                        htMask.push(mask);
                    }
                }

                break;
            }

            if (changed) {
                return i3;
            } else {
                return -1;
            }
        },

        p_findXyzWings: function (round) {

            //
            // Find XYZ Wings pattern.
            //

            var c = [], idx = [], c2 = [], idx2 = [];

            for (var box = 0; box < 9; box++) {

                solver.getCandidateListOfBox(box, c, idx);

                for (var cell = 0; cell < 9; cell++) {

                    //
                    // Each wing only contains 2 candidates.
                    //

                    if (solver.isSolved(idx[cell]) || 3 != solver.bc(c[cell])) {
                        continue;
                    }

                    for (var cell2 = 0; cell2 < 9; cell2++) {

                        if (cell2 == cell || solver.isSolved(idx[cell2]) || 2 != bc(c[cell2])) {
                            continue;
                        }

                        if (0 == (c[cell] & c[cell2]) || 2 != solver.bc(c[cell] & c[cell2])) {
                            continue;
                        }

                        //
                        // Find 3rd cell.
                        //

                        var col = solver.COL(idx[cell]), row = solver.ROW(idx[cell]);
                        var col2 = solver.COL(idx[cell2]), row2 = solver.ROW(idx[cell2]);

                        if (0 == (c[cell] ^ c[cell2])) { // Make sure c1&c2 have common candidates.
                            continue;
                        }

                        var i3, mask;

                        if (col != col2) {
                            solver.getCandidateListOfCol(col, c2, idx2);
                            i3 = solver.findXyzWings(c2, idx2, box, true, row, idx[cell], idx[cell2], c[cell], c[cell2], mask);
                        }

                        if (row != row2) {
                            solver.getCandidateListOfRow(row, c2, idx2);
                            i3 = solver.findXyzWings(c2, idx2, box, false, col, idx[cell], idx[cell2], c[cell], c[cell2], mask);
                        }

                        if (-1 == i3) {
                            continue;
                        }

                        //
                        // State changed.
                        //

                        ht1.push(idx[cell]);
                        ht1.push(idx[cell2]);
                        ht1.push(i3);

                        msg = comm.tech.XYZ_Wing;// 'XYZ-Wings';
                        return true;
                    }
                }
            }

            return false;
        },

        hasLink: function (a, b) {
            return solver.BOX(a) == solver.BOX(b) || solver.COL(a) == solver.COL(b) || solver.ROW(a) == solver.ROW(b);
        },

        p_findXChains: function (round) {

            //
            // Find X chains pattern.
            //

            var c = [], idx = [], cell = [];
            var cx, x = [];

            for (var n = 1; n <= 9; n++) {

                cx = 0;

                //
                // Collect strong links.
                //

                for (var row = 0; row < 9; row++) {
                    solver.getCandidateListOfRow(row, c, idx);
                    if (2 == solver.getCandidateCountOfList(c, n, cell)) {
                        var i1 = idx[cell[0]], i2 = idx[cell[1]];
                        if (solver.BOX(i1) != solver.BOX(i2)) {
                            x[cx * 2 + 0] = i1;
                            x[cx * 2 + 1] = i2;
                            cx += 1;
                        }
                    }
                }

                for (var col = 0; col < 9; col++) {
                    solver.getCandidateListOfCol(col, c, idx);
                    if (2 == solver.getCandidateCountOfList(c, n, cell)) {
                        var i1 = idx[cell[0]], i2 = idx[cell[1]];
                        if (solver.BOX(i1) != solver.BOX(i2)) {
                            x[cx * 2 + 0] = i1;
                            x[cx * 2 + 1] = i2;
                            cx += 1;
                        }
                    }
                }

                for (var box = 0; box < 9; box++) {
                    solver.getCandidateListOfBox(box, c, idx);
                    if (2 == solver.getCandidateCountOfList(c, n, cell)) {
                        x[cx * 2 + 0] = idx[cell[0]];
                        x[cx * 2 + 1] = idx[cell[1]];
                        cx += 1;
                    }
                }

                if (2 > cx) {
                    continue;
                }

                //
                // Find strong link chain.
                //

                for (var i = 0; i < cx; i++) {

                    var len = 0;                    // Length of the chain.
                    var chain = [];

                    var linked = []                 // Flags for attached links.
                    for (var j = 0; j < 81; j++) {
                        linked[j] = false;
                    }

                    var a = x[i * 2 + 0];           // Start-end points of the chain.
                    var b = x[i * 2 + 1];

                    linked[i] = true;
                    chain[len++] = i;

                    while (true) {
                        var savLen = len;
                        for (var j = 0; j < cx; j++) {

                            var gotNewLink = false;
                            if (linked[j]) {
                                continue;
                            }

                            var ci = x[j * 2 + 0];
                            var d = x[j * 2 + 1];

                            //
                            // Two links can't connect physically.
                            //

                            var dup = false;
                            for (var k = 0; k < len; k++) {
                                if (ci == x[2 * chain[k]] || d == x[2 * chain[k]] ||
                                    ci == x[2 * chain[k] + 1] || d == x[2 * chain[k] + 1]) {
                                    dup = true;
                                    break;
                                }
                            }
                            if (dup) {
                                continue;
                            }

                            //
                            // Check is there a new link, if so change the start-end points of
                            // the chain.
                            //

                            if (solver.hasLink(a, ci) && solver.BOX(b) != solver.BOX(d)) {
                                a = d;
                                gotNewLink = true;
                            } else if (solver.hasLink(a, d) && solver.BOX(b) != solver.BOX(ci)) {
                                a = ci;
                                gotNewLink = true;
                            } else if (solver.hasLink(b, ci) && solver.BOX(a) != solver.BOX(d)) {
                                b = d;
                                gotNewLink = true;
                            } else if (solver.hasLink(b, d) && solver.BOX(a) != solver.BOX(ci)) {
                                b = ci;
                                gotNewLink = true;
                            }

                            if (!gotNewLink) {
                                continue;
                            }

                            linked[j] = true;
                            chain[len++] = j;

                            //
                            // Check changes.
                            //

                            if (!solver.isChanged(a, b, solver.n2b(n))) {
                                continue;
                            }

                            ht1.push(a);
                            ht1.push(b);

                            for (var k = 0; k < len; k++) {
                                ht4.push(x[2 * chain[k]]);
                                ht4.push(x[2 * chain[k] + 1]);
                                ht4.push(n);
                            }

                            msg = comm.tech.X_Chains;// 'X-Chains';
                            return true;
                        }

                        if (len == savLen) {          // No new link found.
                            break;
                        }
                    }
                }

            }

            return false;
        },

        c2b: function (c, n) {
            //
            // Get n-th bit 1 of c.
            //

            for (var i = 0, mask = 1; i < 9; i++, mask <<= 1) {
                if (c & mask && 0 == --n) {
                    return mask;
                }
            }
            return 0;
        },

        findXyChains: function (round, link) {
            //
            // Check is current chain valid.
            //

            if (2 < xyNChain && link == xyMask) {

                var a = xyChain[0];               // Head.
                var b = xyChain[xyNChain - 1];    // Tail.

                if (0 != (candidate[b] & xyMask) && solver.isChanged(a, b, xyMask)) {

                    ht1.push(a);
                    ht1.push(b);

                    var mask = xyMask;
                    for (var i = 0; i < xyNChain - 1; i++) {
                        ht4.push(xyChain[i]);
                        ht4.push(xyChain[i + 1]);
                        var c = candidate[xyChain[i]];
                        var l1 = solver.c2b(c, 1),
                            l2 = solver.c2b(c, 2);
                        var mask1 = l1 == mask ? l2 : l1;
                        if (0 == mask1) {
                            c = candidate[xyChain[i + 1]];
                            l1 = solver.c2b(c, 1), l2 = c2b(c, 2);
                            mask1 = l1 == mask ? l2 : l1;
                        }
                        ht4.push(solver.b2n(mask1));
                        mask = mask1;
                    }

                    msg = comm.tech.XY_Chains;// 'XY-Chains';
                    return true;
                }
            }

            //
            // Backtracking.
            //

            for (var i = 0; i < xyNCell; i++) {
                if (xyFlag[i]) {
                    continue;
                }
                var cell = xyCell[i];
                if (!solver.hasLink(xyChain[xyNChain - 1], cell)) {
                    continue;
                }
                var c = candidate[cell];
                if (0 == (c & link)) {
                    continue;
                }
                xyFlag[i] = true;
                xyChain[xyNChain++] = cell;
                var link1 = solver.c2b(c, 1), link2 = solver.c2b(c, 2);
                if (link == link1) {
                    if (solver.findXyChains(round, link2)) {
                        return true;
                    }
                } else {
                    if (solver.findXyChains(round, link1)) {
                        return true;
                    }
                }
                xyFlag[i] = false;
                xyNChain -= 1;
            }
            return false;
        },

        p_findXyChains: function (round) {
            xyNCell = xyNChain = 0;

            //
            // Collect cells that have 2 candidates.
            //

            for (var i = 0; i < 81; i++) {
                if (2 == solver.bc(candidate[i])) {
                    xyCell[xyNCell] = i;
                    xyFlag[xyNCell] = false;
                    xyNCell += 1;
                }
            }

            //
            // Backtracking to find a XY chain.
            //

            for (var i = 0; i < xyNCell; i++) {
                xyFlag[i] = true;
                var cell = xyCell[i];
                xyChain[xyNChain++] = cell;
                var c = candidate[cell];
                var link1 = solver.c2b(c, 1), link2 = solver.c2b(c, 2);
                xyMask = link2;
                if (solver.findXyChains(round, link1)) {
                    return true;
                }
                xyMask = link1;
                if (solver.findXyChains(round, link2)) {
                    return true;
                }
                xyFlag[i] = false;
                xyNChain -= 1;
            }

            return false;
        }
    };